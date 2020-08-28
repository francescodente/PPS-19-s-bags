package sbags.interaction.view

import sbags.model.core.Failure

/**
 * The GUI displaying the game.
 *
 * @tparam G type of the game state.
 */
trait GameView[G] extends SubView[GameViewHandler] {
  protected val renderers: Seq[Renderer[G]]

  /**
   * Notifies the View that the last inputted move was accepted.
   *
   * @param gameState the game state after the last action was executed.
   */
  def moveAccepted(gameState: G): Unit

  /** Notifies the View that the last inputted move was rejected. */
  def moveRejected(failure: Failure): Unit

  /**
   * Renders the game.
   *
   * @param gameState the game state to be displayed.
   */
  protected def render(gameState: G): Unit = renderers.foreach(_.render(gameState))
}

/** Gets executed when a new user interaction happened. */
trait GameViewHandler {
  /**
   * Handles the [[sbags.interaction.view.Event]] emitted by the user interface.
   *
   * @param event the [[sbags.interaction.view.Event]] emitted by the user interface.
   */
  def onEvent(event: Event)
}
