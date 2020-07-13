package sbags.interaction.view

import sbags.interaction.controller.InputListener

trait View[G] {
  def addListener(listener: InputListener): Unit
  def moveAccepted(gameState: G): Unit
  def moveRejected(): Unit
}
