package sbags.entity

/**
 * Represents part of the Game Flow, namely the succession of game turns as defined by the user.
 * @tparam T the type of the Turn.
 */
trait Turns[T] extends GameState {

  /**
   * Returns an Optional containing the current turn if any, None otherwise.
   * @return an Optional containing the current turn if any, None otherwise.
   */
  def turn: Option[T]

  /**
   * Makes the game state transition into the next turn.
   */
  def nextTurn(): Unit
}

/**
 * Represents an implementation of [[sbags.entity.Turns]] using Scala's stream.
 * @tparam T the type of the Turn.
 */
trait TurnsStream[T] extends Turns[T] {
  override def turn: Option[T] = remainingTurns.headOption

  /**
   * Holds the stream of remaining turns.
   * This must be initialized when using the mixin.
   */
  protected var remainingTurns: Stream[T]

  override def nextTurn(): Unit = remainingTurns = remainingTurns.tail
}

/**
 * Represents a game specification where two players take alternate turns.
 * @tparam P defines the type of the [[sbags.entity.Players]], which is the same of the Turn.
 */
trait TwoPlayersAlternateTurn[P] extends Turns[P] with Players[P] {

  private var currentPlayer: Int = 0

  /**
   * Holds the two players' representation.
   * This must be initialized when using the mixin.
   */
  val playersPair: (P, P)

  def players: Set[P] = Set(playersPair._1, playersPair._2)

  def turn: Option[P] = currentPlayer match {
    case 0 => Some(playersPair._1)
    case 1 => Some(playersPair._2)
  }

  def nextTurn(): Unit = currentPlayer match {
    case 0 => currentPlayer = 1
    case 1 => currentPlayer = 0
  }
}

/**
 * Represents a game specification where each move ends the current turn.
 * @tparam T the type of the Turn.
 */
trait EndTurnAfterEachMove[T] extends Turns[T] {
  override def executeMove(move: Move): Unit = {
    super.executeMove(move)
    nextTurn()
  }
}
