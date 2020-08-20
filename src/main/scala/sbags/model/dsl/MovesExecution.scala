package sbags.model.dsl

import scala.reflect.ClassTag

/**
 * Enables words to work with the moves execution.
 *
 * @tparam M type of moves.
 * @tparam G type of the game state.
 */
trait MovesExecution[M, G] {
  private var movesExe: List[PartialFunction[M, G => G]] = List.empty
  private var optionalBeforeExecution: List[(M => Boolean, G => G)] = List.empty
  private var optionalAfterExecution: List[(M => Boolean, G => G)] = List.empty

  private def addMoveExe(moveExe: PartialFunction[M, G => G]): Unit = movesExe = movesExe :+ moveExe

  /**
   * Applies a move to a state.
   *
   * @param move the move to be done.
   * @param state the state at which apply the move.
   * @return a new state.
   */
  def collectMovesExecution(move: M)(state: G): G = {
    val doNothing: PartialFunction[M, G => G] = {
      case _ => s => s
    }
    def filter(optionalAction: List[(M => Boolean, G => G)], move: M): List[G => G] = {
      for ((cond, f) <- optionalAction; if cond(move)) yield f
    }

    val m: G => G = movesExe.foldRight(doNothing)(_ orElse _)(move)

    val beforeEachExecution = filter(optionalBeforeExecution, move)
    val afterEachExecution = filter(optionalAfterExecution, move)
    val operations: List[G => G] = beforeEachExecution ++ (m :: afterEachExecution)
    (operations reduceLeft (_ andThen _)) (state)
  }

  /**
   * Enables syntax to declare moves executions.
   *
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   onMove (SomeMove) {...}
   *   ^
   *   onMove ofType[M] {...}
   *   ^
   *   onMove matching {...}
   *   ^
   * }}}
   */
  object onMove {
    /**
     * Matches a specific move.
     *
     * @param m a specific move.
     * @param action the action to be performed.
     */
    def apply(m: M)(action: G => G): Unit =
      addMoveExe { case `m` => action }

    /**
     * Matches a move using a partial function.
     *
     * @param f the partial function to be used.
     */
    def matching(f: PartialFunction[M, G => G]): Unit =
      addMoveExe(f)

    /**
     * Matches a move of a specific type.
     *
     * @param action the action to be performed.
     * @tparam Move type of moves.
     */
    def ofType[Move <: M : ClassTag](action: G => G): Unit =
      addMoveExe { case _: Move => action }
  }

  /** Represents the moment in which to perform an action. */
  sealed trait Moment {
    def each(t: (MovePredicate, G => G)): Unit
  }

  /** Enables syntax to declare an operation to be done before each move execution.
   *
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   before each {...}
   *   ^
   * }}}
   */
  case object before extends Moment {
    override def each(t: (MovePredicate, G => G)): Unit = t._1 match {
      case m: MovePredicate => optionalBeforeExecution = optionalBeforeExecution :+ (m.isValid _ -> t._2)
    }
  }

  /**
   * Enables syntax to declare an operation to be done after each move execution.
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   after each {...}
   *   ^
   * }}}
   */
  case object after extends Moment {
    override def each(t: (MovePredicate, G => G)): Unit = t._1 match {
      case m: MovePredicate => optionalAfterExecution = optionalAfterExecution :+ (m.isValid _ -> t._2)
    }
  }

  sealed trait MovePredicate {
    def isValid(move: M): Boolean
  }

  /**
   * Specific the move properties.
   *
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   after each move -> (...)
   *              ^
   * }}}
   *
   * or
   *
   * {{{
   *   before each move.ofType[M] -> (...)
   *               ^
   * }}}
   *
   * or
   *
   * {{{
   *   after each move(SomeMove) -> (...)
   *              ^
   * }}}
   *
   */
  case object move extends MovePredicate {
    def apply(move: M): MovePredicate = specificMove(move)
    def ofType[T <: M : ClassTag]: MovePredicate = moveOfType()
    override def isValid(move: M): Boolean = true
  }

  private case class specificMove(m: M) extends MovePredicate {
    override def isValid(move: M): Boolean = move == m
  }

  private case class moveOfType[T <: M: ClassTag]() extends MovePredicate {
    override def isValid(move: M): Boolean = move match {
      case _: T => true
      case _ => false
    }
  }
}
