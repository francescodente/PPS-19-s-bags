package sbags.core.dsl

import scala.reflect.ClassTag

trait MovesExecution[M, G] {
  private var movesExe: List[PartialFunction[M, G => G]] = List.empty
  private var optionalBeforeExecution: List[(M => Boolean, G => G)] = List.empty
  private var optionalAfterExecution: List[(M => Boolean, G => G)] = List.empty
  private def addMoveExe(moveExe: PartialFunction[M, G => G]): Unit = movesExe = movesExe :+ moveExe

  def collectMovesExecution(move: M)(state: G): G = {
    val doNothing: PartialFunction[M, G => G] = {
      case _ => s => s
    }
    def filter(optionalAction: List[(M => Boolean, G => G)], move: M) = {
      for ((cond, f) <- optionalAction; if cond(move)) yield f
    }

    val m: G => G = movesExe.foldRight(doNothing)(_ orElse _)(move)

    val beforeEachExecution = filter(optionalBeforeExecution, move)
    val afterEachExecution = filter(optionalAfterExecution, move)
    val operations: List[G => G] = beforeEachExecution ++ (m :: afterEachExecution)
    (operations reduceLeft (_ andThen _)) (state)
  }

  object onMove {
    def apply(m: M): (G => G) => Unit =
      g => addMoveExe { case `m` => g }

    def matching(f: PartialFunction[M, G => G]): Unit =
      addMoveExe(f)

    def ofType[Move <: M : ClassTag](move: G => G): Unit =
      addMoveExe { case _: Move => move }
  }

  sealed trait Preposition {
    def each(t: (ActionType, G => G)): Unit
  }

  case object before extends Preposition {
    override def each(t: (ActionType, G => G)): Unit = t._1 match {
      case m: ActionType => optionalBeforeExecution = optionalBeforeExecution :+ (m.isValid _ -> t._2)
    }
  }

  case object after extends Preposition {
    override def each(t: (ActionType, G => G)): Unit = t._1 match {
      case m: ActionType => optionalAfterExecution = optionalAfterExecution :+ (m.isValid _ -> t._2)
    }
  }

  sealed trait ActionType {
    def isValid(move: M): Boolean
  }
  case object move extends ActionType {
    def apply(move: M): ActionType = specificMove(move)
    def ofType[T <: M : ClassTag]: moveOfType[T] = moveOfType()
    override def isValid(move: M): Boolean = true
  }

  case class specificMove(m: M) extends ActionType {
    override def isValid(move: M): Boolean = move == m
  }

  case class moveOfType[T <: M: ClassTag]() extends ActionType {
    override def isValid(move: M): Boolean = move match {
      case _: T => true
      case _ => false
    }
  }
}
