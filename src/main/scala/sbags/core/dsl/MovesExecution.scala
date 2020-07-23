package sbags.core.dsl

import scala.reflect.ClassTag

trait MovesExecution[M, G] {
  private var movesExe: List[PartialFunction[M, G => G]] = List()
  private def addMoveExe(moveExe: PartialFunction[M, G => G]): Unit = movesExe = movesExe :+ moveExe

  def collectMovesExecution(move: M)(state: G): G = {
    val doNothing: PartialFunction[M, G => G] = {
      case _ => s => s
    }
    movesExe.foldRight(doNothing)(_ orElse _)(move)(state)
  }

  object onMove {
    def apply(m: M): (G => G) => Unit =
      g => addMoveExe { case `m` => g }

    def matching(f: PartialFunction[M, G => G]): Unit =
      addMoveExe(f)

    def ofType[Move <: M : ClassTag](move: G => G): Unit =
      addMoveExe { case _: Move => move }
  }
}
