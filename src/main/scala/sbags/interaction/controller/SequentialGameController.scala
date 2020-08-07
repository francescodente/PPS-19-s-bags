package sbags.interaction.controller

import sbags.core.Game
import sbags.interaction.view.{Event, GameView, GameViewListener, Quit}

/**
 * An InputListener that requires events to be sent in a specific order, and requires a strategy for translating sequences of events into moves.
 * @param game         the [[sbags.core.Game]] to be handled.
 * @param eventsToMove a function that transforms a List of [[Event]]s into a Move if any, None otherwise.
 * @tparam G the game state type.
 * @tparam M the type of the moves in the game.
 */
class SequentialGameController[M, G](view: GameView[G], game: Game[M, G], eventsToMove: List[Event] => Option[M])
  extends GameViewListener {
  private var events: List[Event] = List()

  override def onEvent(event: Event): Unit = event match {
    case Quit => view.stopGame()
    case _ =>
      events = event :: events
      for (move <- eventsToMove(events)) {
        events = List.empty
        game.executeMove(move) match {
          case Right(gameState) => view moveAccepted gameState
          case Left(failure) => view moveRejected failure
        }
      }
      view.nextCommand()
  }
}
