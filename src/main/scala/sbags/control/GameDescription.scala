package sbags.control

import sbags.{control, entity}

trait GameDescription {
  type GameState
  def newGame: GameState
}

trait BoardGameDescription extends GameDescription {
  type Board <: entity.Board
  type GameState <: control.GameState[Board]
}
