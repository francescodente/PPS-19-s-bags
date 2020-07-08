package sbags.entity

trait Turns[B <: Board, T] { self:BoardGameState[B] =>
  def turn: Option[T]
  def nextTurn():Unit
}

trait TurnsStream[B <: Board, T] extends Turns[B, T] { self:BoardGameState[B] =>
  override def turn:Option[T] = remainingTurns.headOption
  protected var remainingTurns: Stream[T]

  override def nextTurn(): Unit = remainingTurns match {
    case _ #:: t => remainingTurns = t
  }
}

trait GameEndCondition[B <: Board, R] { self:BoardGameState[B] =>
  def gameResult(): R
}