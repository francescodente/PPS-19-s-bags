package sbags.interaction.view

import sbags.core.GameState
import sbags.interaction.controller.InputListener

trait View {
  type State <: GameState
  def addListener(listener: InputListener): Unit
  def moveAccepted(gameState: State): Unit
  def moveRejected(): Unit
}

abstract class BasicView extends View {
  protected var listenerSet: Set[InputListener] = Set.empty
  override def addListener(listener: InputListener): Unit = listenerSet += listener
}
