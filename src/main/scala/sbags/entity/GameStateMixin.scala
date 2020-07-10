package sbags.entity

import sbags.utils.Utility

/**
 * Represents part of the Game Flow, namely the succession of game turns as defined by the user.
 */
trait Turns extends GameState {
  type Turn

  /**
   * Returns an Optional containing the current turn if any, None otherwise.
   * @return an Optional containing the current turn if any, Non otherwise.
   */
  def turn: Option[Turn]

  /**
   * Makes the game state transition into the next turn.
   */
  def nextTurn(): Unit
}

/**
 * Represents an implementation of [[sbags.entity.Turns]] using Scala's stream.
 */
trait TurnsStream extends Turns {
  override def turn: Option[Turn] = remainingTurns.headOption

  /**
   * Holds the stream of remaining turns.
   * This must be initialized when using the mixin.
   */
  protected var remainingTurns: Stream[Turn]

  override def nextTurn(): Unit = remainingTurns = remainingTurns.tail
}

/**
 * Represents a game specification where two players take alternate turns.
 * This trait extends [[sbags.entity.Turns]] and is mixed with [[sbags.entity.Players]].
 * @tparam P defines the type of the [[sbags.entity.Players]].
 */
trait TwoPlayersAlternateTurn[P] extends Turns with Players[P] {
  type Turn = P

  /**
   * Indicates which is the active player.
   * currentPlayer should only be 0 or 1.
   */
  private var currentPlayer: Int = 0

  /**
   * Holds the two players' representation.
   * This must be initialized when using the mixin.
   */
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

/**
 * Defines a concept that decides, after making a Move, if the game should end and in that case what the result should be.
 * @tparam R the type of the Result of the game.
 */
trait GameEndCondition[R] extends GameState {
  /**
   * Returns an Optional containing a Result if any, None otherwise.
   * @return an Optional containing a Result if any, None otherwise.
   */
  def gameResult: Option[R]
  override def executeMove(move: Move): Boolean =
    gameResult.isEmpty && super.executeMove(move)
}

/**
 * Represents a game specification where each move ends the current turn.
 * This trait extends [[sbags.entity.Turns]].
 */
trait EndTurnAfterEachMove extends Turns {
  override def executeMove(move: Move): Boolean = {
    Utility.isActionInvoked(super.executeMove(move))(nextTurn())
  }
}
