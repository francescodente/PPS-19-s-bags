package sbags.interaction

import sbags.interaction.view.{Event, Renderer, RendererBuilder, View}
import sbags.model.core.GameDescription

/**
 * Represents the entry point for each game that extends for it.
 * @tparam M type of move playable in the game.
 */
trait GameSetup[M, G] {
  val gameDescription: GameDescription[M, G]

  val view: View[G]

  /**
   * Defines how each Seq of events is represented as a move
   * @param events the Seq of events
   * @return Optional of move represented from the seq of events, None otherwise.
   */
  def eventsToMove(events: List[Event]): Option[M]
}

trait RenderingSetup[G, R <: Renderer[G]] {
  def setupRenderers(rendering: RendererBuilder[G, R]): RendererBuilder[G, R]
}
