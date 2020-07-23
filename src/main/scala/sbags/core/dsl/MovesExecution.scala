package sbags.core.dsl

import sbags.core.TurnState

import scala.reflect.ClassTag

trait MovesExecution[M, G] {
  private var movesExe: List[PartialFunction[M, G => G]] = List.empty
  private var beforeEachExecution: List[G => G] = List.empty
  private var afterEachExecution: List[G => G] = List.empty
  private def addMoveExe(moveExe: PartialFunction[M, G => G]): Unit = movesExe = movesExe :+ moveExe

  def collectMovesExecution(move: M)(state: G): G = {
    val doNothing: PartialFunction[M, G => G] = {
      case _ => s => s
    }
    val m: G => G = movesExe.foldRight(doNothing)(_ orElse _)(move)
    val operations: List[G => G] = beforeEachExecution ++ (m :: afterEachExecution)
    (operations reduce (_ andThen _)) (state)
  }

  object onMove {
    def apply(m: M): (G => G) => Unit =
      g => addMoveExe { case `m` => g }

    def matching(f: PartialFunction[M, G => G]): Unit =
      addMoveExe(f)

    def ofType[Move <: M : ClassTag](move: G => G): Unit =
      addMoveExe { case _: Move => move }
  }

  sealed trait Moment {
    def each(t: (ActionType, G => G)): Unit
  }

  case object after extends Moment {
    override def each(t: (ActionType, G => G)): Unit = t._1 match {
      case `move` => afterEachExecution = afterEachExecution :+ t._2
    }
  }

  case object before extends Moment {
    override def each(t: (ActionType, G => G)): Unit = t._1 match {
      case `move` => beforeEachExecution = beforeEachExecution :+ t._2
    }
  }

  sealed trait ActionType
  case object move extends ActionType

  def changeTurn[T](implicit ts: TurnState[T, G]): G => G = g => ts.nextTurn(g)
}
