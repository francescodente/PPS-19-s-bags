package sbags.interaction.controller

trait InputListener {
  def notify(event: Event)
}

trait Event
