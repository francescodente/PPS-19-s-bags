package sbags.interaction.controller

import sbags.core.Game

trait GameController[G, M] {
  /**
   * Executes a move on the Game the controller handles.
   * @param move to be executed.
   * @return Either an [[sbags.interaction.controller.InvalidMove]] or a Game State.
   */
  def executeMove(move: M) : Either[InvalidMove.type, G]
}

/**
 * A Game Controller that provides InvalidMove on Exceptions while executing the move, the resulting Game State otherwise.
 * @param game The game handled by the controller.
 * @tparam G Type of the GameState.
 * @tparam M Type of the Move.
 */
class BasicGameController[G, M](game: Game[G, M]) extends GameController[G, M] {
  override def executeMove(move: M): Either[InvalidMove.type, G] =
    try {
      game.executeMove(move)
      Right(game.currentState)
    } catch {
      case ex: Throwable => Left(InvalidMove)
    }
}

/**
 * Represents an InvalidMove if the handled [[sbags.core.Game]] in [[sbags.interaction.controller.GameController]] is provided an invalid move.
 */
case object InvalidMove