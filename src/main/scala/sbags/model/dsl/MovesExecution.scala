package sbags.model.dsl

import scala.reflect.ClassTag

/**
 * Enables syntax to collect work with the moves execution rules.
 *
 * @tparam M the type of moves.
 * @tparam G the type of the game state.
 */
trait MovesExecution[M, G] {
  private var execution: PartialFunction[M, G => G] = PartialFunction.empty
  private var beforeExecution: List[G => G] = List.empty
  private var afterExecution: List[G => G] = List.empty

  private def addMoveExecution(moveExe: PartialFunction[M, G => G]): Unit = execution = execution orElse moveExe

  /**
   * Applies the collected execution rules for the given move to a state.
   *
   * @param move the move to be made.
   * @param state the state on which to apply the move.
   * @return a new state.
   */
  def collectMovesExecution(move: M)(state: G): G = {
    val doNothing: PartialFunction[M, G => G] = {
      case _ => s => s
    }

    val m: G => G = (execution orElse doNothing)(move)
    val operations: List[G => G] = beforeExecution ++ (m :: afterExecution)
    (operations reduceLeft (_ andThen _)) (state)
  }

  /**
   * Enables syntax to declare rules for moves execution.
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
    def apply(m: M)(action: G => G): Unit = addMoveExecution { case `m` => action }

    /**
     * Matches a move using a partial function.
     *
     * @param f the partial function to be used.
     */
    def matching(f: PartialFunction[M, G => G]): Unit = addMoveExecution(f)

    /**
     * Matches a move of a specific type.
     *
     * @param action the action to be performed.
     * @tparam Move type of moves.
     */
    def ofType[Move <: M : ClassTag](action: G => G): Unit = addMoveExecution { case _: Move => action }
  }

  /** Represents a moment in which to perform an action. */
  sealed trait Moment {
    def eachMove(action: G => G): Unit
  }

  /**
   * Enables syntax to declare an operation to be done before each move execution.
   *
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   before eachMove {...}
   *   ^
   * }}}
   */
  case object before extends Moment {
    override def eachMove(action: G => G): Unit = beforeExecution = beforeExecution :+ action
  }

  /**
   * Enables syntax to declare an operation to be done after each move execution.
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   after eachMove {...}
   *   ^
   * }}}
   */
  case object after extends Moment {
    override def eachMove(action: G => G): Unit = afterExecution = afterExecution :+ action
  }
}
