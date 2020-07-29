package sbags.interaction.controller

import sbags.core.Game
import sbags.core.extension.GameEndCondition
import sbags.interaction.view.GameView

/**
 * Gets notified by the view when a new user interaction happened.
 */
trait Controller {
  /**
   * Enables users' interaction with the game.
   */
  def startGame()

  /**
   * Handles the [[sbags.interaction.controller.Event]] emitted by the user interface.
   * @param event the [[sbags.interaction.controller.Event]] emitted by the user interface.
   */
  def notify(event: Event)
}

/**
 * A [[sbags.interaction.controller.Controller]] that can know if a game is ended.
 * @param condition GameEndCondition in which is defined the result of the game
 * @tparam G Type of the GameState.
 */
abstract class TerminatingController[G](implicit condition: GameEndCondition[_,G]) extends Controller {
  /**
   * Use to know if the state passed as input is a final state.
   * @param state the state of the game
   * @return true if the game has a result else false
   */
  protected def gameEnded(state: G): Boolean = condition.gameResult(state).isDefined
}

/**
 * An InputListener that requires events to be sent in a specific order, and requires a strategy for translating sequences of events into moves.
 * @param game the [[sbags.core.Game]] to be handled.
 * @param eventsToMove a function that transforms a List of [[sbags.interaction.controller.Event]]s into a Move if any, None otherwise.
 * @tparam G the game state type.
 * @tparam M the type of the moves in the game.
 */
class SequentialController[G, M](view: GameView[G], game: Game[G, M], eventsToMove: List[Event] => Option[M])
                                (implicit gameEnd: GameEndCondition[_,G])
  extends TerminatingController[G] {
  private val gameController = MoveExecutor(game)
  private var events: List[Event] = List()

  override def notify(event: Event): Unit = {
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
    checkGameEndedOrElse(view nextCommand())
  }

  private def checkGameEndedOrElse(elseBranch: => Unit): Unit =
    if (gameEnded(game.currentState)) view.stopGame()
    else elseBranch

  /**
   * Starts the GUI so the user can interact with the game through it.
   */
  override def startGame(): Unit = checkGameEndedOrElse(view.startGame(game.currentState))
}
