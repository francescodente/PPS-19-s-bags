package sbags.entity

trait Turns[B <: Board] { self:BoardGameState[B] =>
  type Turn
  def turn: Option[Turn]
  def nextTurn():Unit
}

trait TurnsStream[B <: Board] extends Turns[B] { self:BoardGameState[B] =>

  override def turn:Option[Turn] = remainingTurns.headOption
  protected var remainingTurns: Stream[Turn]

  override def nextTurn(): Unit = remainingTurns match {
    case _ #:: t => remainingTurns = t
  }
}

trait GameEndCondition[B <: Board] { self:BoardGameState[B] =>
  type Result
  def gameResult(): Result
}