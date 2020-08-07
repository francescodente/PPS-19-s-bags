package sbags.interaction.view.cli

import sbags.core.GameDescription
import sbags.interaction.controller.ApplicationController
import sbags.interaction.view.RendererBuilder
import sbags.interaction.{GameSetup, SetupRenderers}

abstract class CliGameSetup[M, G](gameDescription: GameDescription[M, G]) extends GameSetup[M] with SetupRenderers[G, CliRenderer[G]] {
  def cliEventParser: CliEventParser = CliEventParser()

  private val renderers = setupRenderers(RendererBuilder()).renderers
  private val stateToGameView = CliGameView[G](renderers, cliEventParser, _)
  private val view = new CliView(stateToGameView)
  new ApplicationController[M, G](gameDescription, view, this).start()
}
