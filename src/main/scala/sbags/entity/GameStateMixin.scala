package sbags.entity

trait Turns extends GameState {
  type Turn
  def turn: Option[Turn]
  def nextTurn(): Unit
}

trait TurnsStream extends Turns {
  override def turn: Option[Turn] = remainingTurns.headOption
  protected var remainingTurns: Stream[Turn]

  override def nextTurn(): Unit = remainingTurns = remainingTurns.tail
}

trait TwoPlayersAlternateTurn[P] extends Turns with Players[P] {
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

trait GameEndCondition[R] extends GameState {
  override def executeMove(move: Move): Unit = {
    if (gameResult.isEmpty) super.executeMove(move)
  }
  def gameResult: Option[R]
}

trait EndTurnAfterEachMove extends Turns {
  override def executeMove(move: Move): Unit = {
    super.executeMove(move)
    nextTurn()
  }
}
