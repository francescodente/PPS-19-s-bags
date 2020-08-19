package sbags.interaction

import sbags.interaction.view.{Event, Renderer, RendererBuilder, View}
import sbags.model.core.GameDescription

/**
 * Defines everything needed to start a new game.
 *
 * @tparam M type of move playable in the game.
 * @tparam G type of game state.
 */
trait GameSetup[M, G] {
  /** Defines the game that represents this setup. */
  val gameDescription: GameDescription[M, G]

  /** Defines what View it's used to render the game.*/
  val view: View[G]

  /**
   * Defines how each Seq of events is represented as a move.
   *
   * @param events the Seq of events
   * @return Optional of move represented from the seq of events, None otherwise.
   */
  def eventsToMove(events: List[Event]): Option[M]
}

/**
 * Defines everything needed to setup a [[sbags.interaction.view.RendererBuilder]].
 *
 * @tparam G type of game state.
 * @tparam R type of renderer built from the RendererBuilder.
 */
trait RenderingSetup[G, R <: Renderer[G]] {
  /**
   * Creates a new [[sbags.interaction.view.RendererBuilder]] from the given one.
   * it's used as hook for library user to update rendererBuilder (e.g. adding some [[sbags.interaction.view.Renderer]]s).
   *
   * @param rendering the rendererBuilder that needs to be updated.
   * @return a new rendererBuilder updated.
   */
  def setupRenderers(rendering: RendererBuilder[G, R]): RendererBuilder[G, R]
}
