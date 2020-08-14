package sbags.interaction.controller

import sbags.interaction.GameSetup

class ApplicationController[M, G](gameSetup: GameSetup[M, G]) {
  def start(): Unit = {
    val menu = gameSetup.view.setupMenu()
    menu.addListener(new MenuController(gameSetup))
    menu.start()
  }
}
