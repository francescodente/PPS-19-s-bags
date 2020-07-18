package sbags.core

import sbags.core.Results.{WinOrDraw, Winner}

trait GameEndCondition[R, G] {
  def gameResult(state: G): Option[R]
}

object GameEndCondition {
  implicit class GameEndConditionOps[R, G](state: G)(implicit ev: GameEndCondition[R, G]) {
    def gameResult: Option[R] = ev.gameResult(state)
  }
}

trait WinCondition[P, G] extends GameEndCondition[Winner[P], G]
trait WinOrDrawCondition[P, G] extends GameEndCondition[WinOrDraw[P], G]
