package sbags.interaction.controller

import sbags.core.{Game, GameEndCondition}
import sbags.interaction.view.View

/**
 * Gets notified by the view when a new user interaction happened.
 */
trait Controller {

  def startGame()

  /**
   * Handles the [[sbags.interaction.controller.Event]] emitted by the user interface.
   * @param event the [[sbags.interaction.controller.Event]] emitted by the user interface.
   */
  def notify(event: Event)
}

abstract class TerminatingController[G](implicit condition: GameEndCondition[_,G]) extends Controller {
  protected def gameEnded(state: G): Boolean = condition.gameResult(state).isDefined
}

/**
 * An InputListener that requires events to be sent in a specific order, and requires a strategy for translating sequences of events into moves.
 * @param game the [[sbags.core.Game]] to be handled.
 * @param eventsToMove a function that transforms a List of [[sbags.interaction.controller.Event]]s into a Move if any, None otherwise.
 * @tparam G the game state type.
 * @tparam M the type of the moves in the game.
 */
class SequentialController[G, M](view: View[G], game: Game[G, M], eventsToMove: List[Event] => Option[M])
                                (implicit gameEnd: GameEndCondition[_,G])
  extends TerminatingController[G] {
  private val gameController = new BasicMoveExecutor(game)
  private var events: List[Event] = List()

  /**
   * For all events that are not [[sbags.interaction.controller.Done]] it just keeps track of them.
   * If the [[sbags.interaction.controller.Done]] event is received, if a valid move exists, it is executed, otherwise it notifies the view that the move was rejected.
   * Finally, the list gets emptied.
   * @param event the [[sbags.interaction.controller.Event]] emitted by the user interface.
   */
  override def notify(event: Event): Unit = {
    event match {
      case Done =>
        eventsToMove(events) match {
          case Some(move) => gameController executeMove move match {
            case Right(gameState) => view moveAccepted gameState
            case Left(_) => view moveRejected()
          }
          case None => view moveRejected()
        }
        events = List.empty
      case _ => events = event :: events
    }
    checkGameEndedOrElse(view nextCommand())
  }

  private def checkGameEndedOrElse(elseBranch: => Unit): Unit =
    if (gameEnded(game.currentState)) view.stopGame()
    else elseBranch

  override def startGame(): Unit = checkGameEndedOrElse(view.startGame(game.currentState))
}
