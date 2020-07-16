package sbags.interaction.view

import sbags.core.GameState

trait View[G <: GameState] {
  def refresh(gameState: G): Unit
  def printError(): Unit
}
