package sbags.interaction.controller

import sbags.core.Game

/**
 * The boundary that connects the [[sbags.interaction.controller]] package with the [[sbags.core]] package,
 * by enabling move execution on the model.
 * @tparam G Game State
 * @tparam M Move
 */
trait MoveExecutor[G, M] {
  /**
   * Executes a move on the Game the controller handles.
   * @param move to be executed.
   * @return Either an [[sbags.interaction.controller.InvalidMove]] or a Game State.
   */
  def executeMove(move: M): Either[InvalidMove.type, G]
}

object MoveExecutor {
  /**
   * A Game Controller that provides InvalidMove on Exceptions while executing the move, the resulting Game State otherwise.
   * @param game The game handled by the controller.
   * @tparam G Type of the GameState.
   * @tparam M Type of the Move.
   */
  class BasicMoveExecutor[G, M](game: Game[G, M]) extends MoveExecutor[G, M] {
    override def executeMove(move: M): Either[InvalidMove.type, G] =
      try {
        game.executeMove(move)
        Right(game.currentState)
      } catch {
        case _: Throwable => Left(InvalidMove)
      }
  }

  def apply[G, M](game: Game[G, M]): MoveExecutor[G, M] = new BasicMoveExecutor(game: Game[G, M])

}

/**
 * Represents an InvalidMove if the handled [[sbags.core.Game]]
 * in [[sbags.interaction.controller.MoveExecutor]] is provided an invalid move.
 */
case object InvalidMove