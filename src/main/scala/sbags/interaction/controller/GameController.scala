package sbags.interaction.controller

import sbags.interaction.view.{Event, GameView, GameViewHandler, Quit, Undo}
import sbags.model.core.{Game, InvalidMove}

/**
 * An InputListener that requires events to be sent in a specific order, and requires a strategy for translating sequences of events into moves.
 *
 * @param game the [[sbags.model.core.Game]] to be handled.
 * @param eventsToMove a function that transforms a List of [[sbags.interaction.view.Event]]s into a Move if any, None otherwise.
 * @tparam G the game state type.
 * @tparam M the type of the moves in the game.
 */
class GameController[M, G](view: GameView[G], game: Game[M, G], eventsToMove: List[Event] => Option[M])
  extends GameViewHandler {
  private var events: List[Event] = List()

  override def onEvent(event: Event): Unit = event match {
    case Quit => view.stopGame()
    case Undo =>
      game.undoLastMove() match {
        case Some(s) => view moveAccepted s
        case _ => view moveRejected InvalidMove
      }
    case _ =>
      events = events :+ event
      for (move <- eventsToMove(events)) {
        events = List.empty
        game.executeMove(move) match {
          case Right(gameState) => view moveAccepted gameState
          case Left(failure) => view moveRejected failure
        }
      }
  }
}
