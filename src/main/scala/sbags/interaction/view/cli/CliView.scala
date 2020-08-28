package sbags.interaction.view.cli

import sbags.interaction.view.{GameView, MenuView, View}

/**
 * Represents a Cli View which has two SubViews: Menu and Game.
 *
 * @param stateToView the function that returns a [[sbags.interaction.view.cli.CliGameView]] from the initial state.
 * @tparam G type of the game state.
 */
class CliView[G](stateToView: G => CliGameView[G]) extends View[G] {

  override def setupMenu(): MenuView = new CliMenuView()

  override def setupGame(initialGameState: G): GameView[G] = stateToView(initialGameState)

  override def close(): Unit = System.exit(0)
}
