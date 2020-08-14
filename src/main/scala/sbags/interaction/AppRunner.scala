package sbags.interaction

import sbags.interaction.controller.ApplicationController

object AppRunner {
  def run[M, G](gameSetup: GameSetup[M, G]): Unit = {
    new ApplicationController(gameSetup).start()
  }
}
