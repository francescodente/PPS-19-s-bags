package sbags.interaction.view

import sbags.interaction.controller.InputListener

trait View[G] {
  protected val renderers: Seq[Renderer[G]]
  def addListener(listener: InputListener): Unit
  def moveAccepted(gameState: G): Unit = renderers.foreach(_.render(gameState))
  def moveRejected(): Unit
  def nextCommand(): Unit
  def startGame(): Unit
}

abstract class BasicView[G] extends View[G] {
  protected var listenerSet: Set[InputListener] = Set.empty
  override def addListener(listener: InputListener): Unit = listenerSet += listener
}
