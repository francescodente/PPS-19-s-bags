package sbags.interaction.controller

import sbags.core.Game
import sbags.interaction.GameSetup
import sbags.interaction.view.{MenuViewListener, View}

class ApplicationController[M, G](view: View[G], newGame: => Game[M, G], gameSetup: GameSetup[M]) {
  def start[R](): Unit = {
    val menu = view.setupMenu()
    menu.addListener(new MenuViewListener {
      override def onStartGame(): Unit = {
        val game = newGame
        val gameView = view.setupGame(game.currentState)
        gameView.addListener(
          new SequentialGameController[M, G](
            gameView,
            game,
            gameSetup.eventsToMove
          )
        )
        gameView.start()
      }

      override def onQuit(): Unit = view.close()
    })
    menu.start()
  }
}
