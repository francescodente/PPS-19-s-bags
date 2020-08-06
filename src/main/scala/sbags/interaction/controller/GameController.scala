package sbags.interaction.controller

import sbags.core.Game
import sbags.interaction.view.{Event, GameView, GameViewListener}


/**
 * A [[sbags.interaction.view.GameViewListener]] that can know if a game is ended.
 *
 * @param gameEnded in which is defined the result of the game
 * @tparam G Type of the GameState.
 */
abstract class TerminatingGameController[G](val gameEnded: G => Boolean) extends GameViewListener

/**
 * An InputListener that requires events to be sent in a specific order, and requires a strategy for translating sequences of events into moves.
 * @param game         the [[sbags.core.Game]] to be handled.
 * @param eventsToMove a function that transforms a List of [[Event]]s into a Move if any, None otherwise.
 * @tparam G the game state type.
 * @tparam M the type of the moves in the game.
 */
class SequentialGameController[M, G](view: GameView[G],
                                     game: Game[M, G],
                                     eventsToMove: List[Event] => Option[M],
                                     gameEnded: G => Boolean)
  extends TerminatingGameController[G](gameEnded) {
  private val gameController = MoveExecutor(game)
  private var events: List[Event] = List()

  override def onEvent(event: Event): Unit = {
    events = event :: events
    eventsToMove(events) match {
      case Some(move) =>
        events = List.empty
        gameController executeMove move match {
          case Right(gameState) => view moveAccepted gameState
          case Left(_) => view moveRejected()
        }
      case None =>
    }
    if (gameEnded(game.currentState)) view.stopGame()
    else view.nextCommand()
  }
}
