package sbags.interaction.controller

import sbags.interaction.GameSetup
import sbags.interaction.view.{MenuViewHandler, View}
import sbags.model.core.GameDescription

/**
 * The controller of the menu phase.
 * It supports handlers for quit and start game events.
 * @param gameSetup the [[sbags.interaction.GameSetup]] for the specific game the controller must handle.
 * @tparam M type of move playable in the game.
 * @tparam G type of the game state.
 */
class MenuController[M, G](gameSetup: GameSetup[M, G]) extends MenuViewHandler {
  override def onStartGame(): Unit = {
    val game = gameSetup.gameDescription.newGame
    val gameView = gameSetup.view.setupGame(game.currentState)
    gameView.addListener(new GameController(gameView, game, gameSetup.eventsToMove))
    gameView.start()
  }

  override def onQuit(): Unit = gameSetup.view.close()
}
