package sbags.control

import sbags.entity.Board

trait BoardGameState[B <: Board] {
  def boardState: B
}

class BasicGameState[B <: Board](val boardState: B) extends BoardGameState[B]

