package sbags.interaction.controller

import sbags.interaction.GameSetup

/**
 * The controller of the application.
 * It sets up and starts the menu.
 *
 * @param gameSetup the [[sbags.interaction.GameSetup]] for the specific game the controller must handle.
 * @tparam M type of move playable in the game.
 * @tparam G type of the game state.
 */
class ApplicationController[M, G](gameSetup: GameSetup[M, G]) {
  def start(): Unit = {
    val menu = gameSetup.view.setupMenu()
    menu.addListener(new MenuController(gameSetup))
    menu.start()
  }
}
