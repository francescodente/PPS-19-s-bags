package sbags.entity

trait Turns[B <: Board, T] { self:BoardGameState[B] =>
  def turn: Option[T]

  def nextTurn():Unit
}

trait GameEndCondition[B <: Board, R] { self:BoardGameState[B] =>
  def gameResult(): R
}