package sbags.interaction.view

import sbags.interaction.controller.InputListener

trait View[G] {
  def addListener(listener: InputListener): Unit
  def moveAccepted(gameState: G): Unit
  def moveRejected(): Unit
}

abstract class BasicView[G] extends View[G] {
  protected var listenerSet: Set[InputListener] = Set.empty
  override def addListener(listener: InputListener): Unit = listenerSet += listener
}
