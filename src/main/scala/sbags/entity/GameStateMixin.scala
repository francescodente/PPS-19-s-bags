package sbags.entity

trait Turns { self:BoardGameState[_] =>
  type Turn
  def turn: Option[Turn]
  def nextTurn():Unit
}

trait TurnsStream extends Turns { self:BoardGameState[_] =>

  override def turn:Option[Turn] = remainingTurns.headOption
  protected var remainingTurns: Stream[Turn]

  override def nextTurn(): Unit = remainingTurns match {
    case _ #:: t => remainingTurns = t
  }
}

trait GameEndCondition { self:BoardGameState[_] =>
  type Result
  def gameResult(): Result
}

trait TwoPlayersAlternateTurn[P] extends Turns with Players[P] { self: BoardGameState[_] =>
  type Turn = P
  private var currentPlayer: Int = 0

  val tuplePlayer: (P, P)

  def players: Set[P] = Set(tuplePlayer._1, tuplePlayer._2)

  def currentTurn: P = turn.get

  def turn: Option[P] = currentPlayer match {
    case 0 => Some(tuplePlayer._1)
    case 1 => Some(tuplePlayer._2)
  }

  def nextTurn(): Unit = currentPlayer match {
    case 0 => currentPlayer = 1
    case 1 => currentPlayer = 0
  }
}
