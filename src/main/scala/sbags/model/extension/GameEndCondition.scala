package sbags.model.extension

import sbags.model.extension.Results.{WinOrDraw, Winner}

trait GameEndCondition[R, G] {
  def gameResult(state: G): Option[R]
}
object GameEndCondition {
  def apply[R, G](stateToResult: G => Option[R]): GameEndCondition[R, G] = stateToResult(_)
}

trait WinCondition[P, G] extends GameEndCondition[Winner[P], G]
object WinCondition {
  def apply[P, G](stateToResult: G => Option[Winner[P]]): WinCondition[P, G] = stateToResult(_)
}

trait WinOrDrawCondition[P, G] extends GameEndCondition[WinOrDraw[P], G]
object WinOrDrawCondition {
  def apply[P, G](stateToResult: G => Option[WinOrDraw[P]]): WinOrDrawCondition[P, G] = stateToResult(_)
}
