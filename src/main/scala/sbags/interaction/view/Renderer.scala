package sbags.interaction.view

import sbags.core.{BoardGameState, GameEndCondition, RectangularBoardStructure, TurnState}

trait Renderer[G] {
  def render(state: G): Unit
}

