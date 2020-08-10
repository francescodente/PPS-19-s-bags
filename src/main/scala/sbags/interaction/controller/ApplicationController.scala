package sbags.interaction.controller

import sbags.interaction.GameSetup
import sbags.interaction.view.View
import sbags.model.core.GameDescription

class ApplicationController[M, G](gameDescription: GameDescription[M, G], view: View[G], gameSetup: GameSetup[M]) {
  def start(): Unit = {
    val menu = view.setupMenu()
    menu.addListener(new MenuController(gameDescription, view, gameSetup))
    menu.start()
  }
}
