package sbags.interaction

import sbags.interaction.view.{Event, Renderer, RendererBuilder}

trait GameSetup[M] extends App {
  def eventsToMove(events: List[Event]): Option[M]
}

trait SetupRenderers[G, R <: Renderer[G]] {
  def setupRenderers(builder: RendererBuilder[G, R]): RendererBuilder[G, R]
}
