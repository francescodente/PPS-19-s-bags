package sbags.interaction.controller

import sbags.core.{Game, GameDescription, GameState}

trait InputListener {
  def notify(event: Event)
}

trait Event

class SequentialInputListener[State <: GameState, Move](game: Game[State, Move], eventsToMove: List[Event] => Option[Move]) extends InputListener {

  private var events: List[Event] = List()

  override def notify(event: Event): Unit = event match {
    case Done =>
      eventsToMove(events).foreach(game executeMove _)
      events = List.empty
    case _ => events = event :: events
  }
}
