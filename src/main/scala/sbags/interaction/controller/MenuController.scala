package sbags.interaction.controller

import sbags.interaction.GameSetup
import sbags.interaction.view.{MenuViewListener, View}
import sbags.model.core.GameDescription

class MenuController[M, G](gameSetup: GameSetup[M, G]) extends MenuViewListener {
  override def onStartGame(): Unit = {
    val game = gameSetup.gameDescription.newGame
    val gameView = gameSetup.view.setupGame(game.currentState)
    gameView.addListener(new GameController(gameView, game, gameSetup.eventsToMove))
    gameView.start()
  }

  override def onQuit(): Unit = gameSetup.view.close()
}
