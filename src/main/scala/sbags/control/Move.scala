package sbags.control

import sbags.entity.Board

trait Move[S <: GameState[Board]] {
  def execute(gameState: S): S
}
