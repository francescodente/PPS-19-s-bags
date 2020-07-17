package sbags.interaction.view

import sbags.core.GameState
import sbags.interaction.controller.InputListener

trait View[S <: GameState] {
  def addListener(listener: InputListener): Unit
  def moveAccepted(gameState: S): Unit
  def moveRejected(): Unit
}

abstract class BasicView[S <: GameState] extends View[S] {
  protected var listenerSet: Set[InputListener] = Set.empty
  override def addListener(listener: InputListener): Unit = listenerSet += listener
}
