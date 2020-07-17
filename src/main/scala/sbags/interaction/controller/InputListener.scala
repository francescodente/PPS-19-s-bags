package sbags.interaction.controller

import sbags.core.{Game, GameState}
import sbags.interaction.view.View

trait InputListener {
  def notify(event: Event)
}

abstract class SequentialInputListener[State <: GameState, Move](game: Game[State, Move],
                                                        eventsToMove: List[Event] => Option[Move]
                                                       ) extends InputListener {
  protected val view: View[State]
  private val gameController = new BasicGameController(game)
  private var events: List[Event] = List()

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
