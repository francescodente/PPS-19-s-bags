package sbags.interaction.controller

import sbags.core.GameDescription
import sbags.interaction.GameSetup
import sbags.interaction.view.{MenuViewListener, View}

class MenuController[M, G](gameDescription: GameDescription[M, G], view: View[G], gameSetup: GameSetup[M])
  extends MenuViewListener {
  override def onStartGame(): Unit = {
    val game = gameDescription.newGame
    val gameView = view.setupGame(game.currentState)
    gameView.addListener(new GameController(gameView, game, gameSetup.eventsToMove))
    gameView.start()
  }

  override def onQuit(): Unit = view.close()
}
