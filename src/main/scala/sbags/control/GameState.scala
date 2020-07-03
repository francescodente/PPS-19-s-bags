package sbags.control

import sbags.entity.Board

trait GameState[B <: Board] {
  def boardState: B
}

class BasicGameState[B <: Board](val boardState: B) extends GameState[B]

