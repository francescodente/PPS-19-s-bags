package sbags.interaction.view.cli

import sbags.interaction.view.{GameView, MenuView, View}

/**
 * Represents a Cli View divided in Menu and Game visual.
 *
 * @param stateToView the function that returns a [[sbags.interaction.view.cli.CliGameView]] from the initial state.
 * @tparam G type of the game state.
 */
class CliView[G](stateToView: G => CliGameView[G]) extends View[G] {
  /**
   * Prepares for use and returns a [[sbags.interaction.view.MenuView]].
   * Defines the type of the MenuView (as a Cli).
   *
   * @return the created MenuView
   */
  override def setupMenu(): MenuView = new CliMenuView()

  /**
   * Prepares for use and returns a [[sbags.interaction.view.GameView]].
   * Defines the type of the GameView (as a Cli).
   *
   * @param initialGameState the first game state to be displayed.
   * @return a [[sbags.interaction.view.GameView]].
   */
  override def setupGame(initialGameState: G): GameView[G] = stateToView(initialGameState)

  /** Stops the program. */
  override def close(): Unit = System.exit(0)
}
