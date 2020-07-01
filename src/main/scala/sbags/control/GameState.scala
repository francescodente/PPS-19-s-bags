package sbags.control

import sbags.entity.Board

trait GameState[T, P] {
  def board: Board[T, P]
}
