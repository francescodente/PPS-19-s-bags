package sbags.control

import sbags.entity.Board

trait GameState[B <: Board] {
  def board: B
}