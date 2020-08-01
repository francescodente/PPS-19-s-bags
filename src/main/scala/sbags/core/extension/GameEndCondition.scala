package sbags.core.extension

import sbags.core.extension.Results.{WinOrDraw, Winner}

trait GameEndCondition[R, G] {
  def gameResult(state: G): Option[R]
}
trait WinCondition[P, G] extends GameEndCondition[Winner[P], G]
trait WinOrDrawCondition[P, G] extends GameEndCondition[WinOrDraw[P], G]