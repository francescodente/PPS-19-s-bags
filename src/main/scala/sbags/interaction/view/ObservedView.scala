package sbags.interaction.view

import sbags.core.GameState
import sbags.interaction.controller.InputListener

trait ObservedView[G <: GameState] {
  def addListener(listener: InputListener): Unit
  def moveAccepted(gameState: G): Unit
  def moveRejected(): Unit
}

abstract class BasicObservedView[G <: GameState] extends ObservedView[G] {
  protected val view: View[G]
  protected var listenerSet: Set[InputListener] = Set.empty

  override def addListener(listener: InputListener): Unit = listenerSet += listener
  override def moveAccepted(gameState: G): Unit = view.refresh(gameState)
  override def moveRejected(): Unit = view.printError()
}
