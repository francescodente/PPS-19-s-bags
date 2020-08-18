package sbags.interaction

import sbags.interaction.controller.ApplicationController

/**
 * Represents the entry point for each game.
 */
object AppRunner {
  /**
   * Starts the game using a [[sbags.interaction.GameSetup]].
   *
   * @param gameSetup defines what game will be started.
   * @tparam M type of move playable in the game.
   * @tparam G type of game state.
   */
  def run[M, G](gameSetup: GameSetup[M, G]): Unit = {
    new ApplicationController(gameSetup).start()
  }
}
