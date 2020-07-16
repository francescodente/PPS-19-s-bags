package sbags.interaction.controller

import sbags.core.{Board, Game, GameDescription, GameState}
import sbags.interaction.view.View

trait InputListener {
  def notify(event: Event)
}

trait Event

class SequentialInputListener[State <: GameState, Move](game: Game[State, Move],
                                                        eventsToMove: List[Event] => Option[Move]
                                                       ) extends InputListener {

  private val gameController = new BasicGameController(game)
  private var events: List[Event] = List()

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
