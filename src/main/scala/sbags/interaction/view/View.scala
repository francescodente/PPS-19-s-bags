package sbags.interaction.view

import sbags.core.GameEndCondition
import sbags.interaction.controller.Controller

trait View[G] {
  protected val renderers: Seq[Renderer[G]]
  def addListener(listener: Controller): Unit
  def moveAccepted(gameState: G): Unit
  def moveRejected(): Unit
  def nextCommand(): Unit
  def startGame(initialGameState: G): Unit
  def shutDown(): Unit
  protected def render(gameState: G): Unit = renderers.foreach(_.render(gameState))
}

trait ListenedView[G] extends View[G] {
  protected var listenerSet: Set[Controller] = Set.empty
  override def addListener(listener: Controller): Unit = listenerSet += listener
}

