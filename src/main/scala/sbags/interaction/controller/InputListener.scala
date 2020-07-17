package sbags.interaction.controller

import sbags.core.{Game, GameState}
import sbags.interaction.view.View

/**
 * Gets notified by the view when a new user interaction happened.
 */
trait InputListener {
  /**
   * Handles the [[sbags.interaction.controller.Event]] emitted by the user interface.
   * @param event the [[sbags.interaction.controller.Event]] emitted by the user interface.
   */
  def notify(event: Event)
}

/**
 * An InputListener that requires events to be sent in a specific order, and requires a strategy for translating sequences of events into moves.
 * @param game the [[sbags.core.Game]] to be handled.
 * @param eventsToMove a function that transforms a List of [[sbags.interaction.controller.Event]]s into a Move if any, None otherwise.
 * @tparam State the [[sbags.core.GameState]] type.
 * @tparam Move the type of the moves in the game.
 */
abstract class SequentialInputListener[State <: GameState, Move](game: Game[State, Move],
                                                        eventsToMove: List[Event] => Option[Move]
                                                       ) extends InputListener {

  protected val view: View[State]
  private val gameController = new BasicGameController(game)
  private var events: List[Event] = List()

  /**
   * For all events that are not [[sbags.interaction.controller.Done]] it just keeps track of them.
   * If the [[sbags.interaction.controller.Done]] event is received, if a valid move exists, it is executed, otherwise it notifies the view that the move was rejected.
   * Finally, the list gets emptied.
   * @param event the [[sbags.interaction.controller.Event]] emitted by the user interface.
   */
  override def notify(event: Event): Unit = event match {
    case Done =>
      eventsToMove(events).foreach(m => {
        gameController executeMove m match {
          case Right(gameState) => view moveAccepted gameState
          case Left(_) => view moveRejected()
        }
      })
      events = List.empty
    case _ => events = event :: events
  }
}
