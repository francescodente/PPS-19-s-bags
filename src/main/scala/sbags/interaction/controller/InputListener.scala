package sbags.interaction.controller

import sbags.core.Game

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
 * An event to be emitted by a user interface, representing a user's interaction.
 * Some examples are selecting a tile, selecting a pawn, ecc.
 * A particularly important type of event is [[sbags.interaction.controller.Done]], which terminates a sequence of events and executes a move if a match is found.
 */
trait Event

/**
 * An InputListener that requires events to be sent in a specific order, and requires a strategy for translating sequences of events into moves.
 * @param game the [[sbags.core.Game]] to be handled.
 * @param eventsToMove a function that transforms a List of [[sbags.interaction.controller.Event]]s into a Move if any, None otherwise.
 * @tparam State the game state type.
 * @tparam Move the type of the moves in the game.
 */
class SequentialInputListener[State, Move](game: Game[State, Move],
                                                        eventsToMove: List[Event] => Option[Move]
                                                       ) extends InputListener {

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
          case Left(gameState) => //TODO: view moveAccepted _
          case Right(_) => //TODO: view moveRejected
        }
      })
      events = List.empty
    case _ => events = event :: events
  }
}
