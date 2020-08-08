package sbags.interaction.controller

import sbags.core.GameDescription
import sbags.interaction.GameSetup
import sbags.interaction.view.View

class ApplicationController[M, G](gameDescription: GameDescription[M, G], view: View[G], gameSetup: GameSetup[M]) {
  def start(): Unit = {
    val menu = view.setupMenu()
    menu.addListener(new MenuController(gameDescription, view, gameSetup))
    menu.start()
  }
}
