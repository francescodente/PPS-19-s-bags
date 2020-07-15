package sbags.interaction.controller

trait InputListener {
  def notify(event: Event)
}

trait Event

class SequentialInputListener[M](eventsToMove: List[Event] => Option[M]) extends InputListener {
  private var events: List[Event] = List()

  override def notify(event: Event): Unit = event match {
    case Done => {
      val move = eventsToMove(events)
      //TODO: execute move
      events = List.empty
    }
    case _ => events = event :: events
  }
}
