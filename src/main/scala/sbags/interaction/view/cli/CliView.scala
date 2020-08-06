package sbags.interaction.view.cli

import sbags.interaction.view.{GameView, MenuView, View}

class CliView[G](stateToView: G => GameView[G]) extends View[G] {
  override def setupMenu(): MenuView = new CliMenuView()
  override def setupGame(initialGameState: G): GameView[G] = stateToView(initialGameState)

  override def close(): Unit = System.exit(0)//todo
}
