package sbags.control

import sbags.entity.Board

trait GameState {
  type GameBoard <: Board

  def board: GameBoard
}
