package sbags.interaction

import sbags.core.GameDescription
import sbags.interaction.controller.ApplicationController
import sbags.interaction.view.cli.{CliEventParser, CliGameView, CliRenderer, CliView}
import sbags.interaction.view.{Event, Renderer, RendererBuilder}

trait GameSetup[M] extends App {
  def eventsToMove(events: List[Event]): Option[M]
}

trait SetupRenderers[G, R <: Renderer[G]] {
  def setupRenderers(builder: RendererBuilder[G, R]): RendererBuilder[G, R]
}

abstract class CliGameSetup[M, G](gameDescription: GameDescription[M, G]) extends GameSetup[M] with SetupRenderers[G, CliRenderer[G]] {
  def cliEventParser: CliEventParser
  private val renderers = setupRenderers(RendererBuilder()).renderers
  private val stateToGameView = CliGameView[G](renderers, cliEventParser, _)
  private val view = new CliView(stateToGameView)
  new ApplicationController[M, G](view, gameDescription.newGame, this).start()
}
