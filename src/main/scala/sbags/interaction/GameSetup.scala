package sbags.interaction

import sbags.interaction.view.{Event, Renderer, RendererBuilder}

/**
 * Represents the entry point for each game that extends for it.
 * @tparam M type of move playable in the game.
 */
trait GameSetup[M] extends App {
  /**
   * Defines how each Seq of events is represented as a move
   * @param events the Seq of events
   * @return Optional of move represented from the seq of events, None otherwise.
   */
  def eventsToMove(events: Seq[Event]): Option[M]
}

trait SetupRenderers[G, R <: Renderer[G]] {
  def setupRenderers(builder: RendererBuilder[G, R]): RendererBuilder[G, R]
}
