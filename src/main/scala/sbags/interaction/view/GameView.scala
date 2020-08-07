package sbags.interaction.view

import sbags.core.Failure


/**
 * The GUI displaying the game.
 *
 * @tparam G type of the game state.
 */
trait GameView[G] extends Startable with Observable[GameViewListener] {
  protected val renderers: Seq[Renderer[G]]

  /**
   * Notifies the View that the last inputted move was accepted.
   * @param gameState the game state after the last action was executed.
   */
  def moveAccepted(gameState: G): Unit

  /**
   * Notifies the View that the last inputted move was rejected.
   */
  def moveRejected(failure: Failure): Unit

  /**
   * Notifies the View that the last [[Event]] was correctly received but a move is not detected yet, so more input is expected.
   */
  def nextCommand(): Unit

  /**
   * Terminates the execution of the View.
   */
  def stopGame(): Unit

  /**
   * Renders the game.
   * @param gameState the game state to be displayed.
   */
  protected def render(gameState: G): Unit = renderers.foreach(_.render(gameState))
}

/**
 * Gets notified by the view when a new user interaction happened.
 */
trait GameViewListener {
  /**
   * Handles the [[Event]] emitted by the user interface.
   * @param event the [[Event]] emitted by the user interface.
   */
  def onEvent(event: Event)
}

