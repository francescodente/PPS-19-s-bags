package sbags.interaction.view

import sbags.interaction.controller.Controller

/**
 * The GUI displaying the game.
 * @tparam G type of the game state.
 */
trait GameView[G] {
  protected val renderers: Seq[Renderer[G]]

  /**
   * Adds a listener to be notified on input events.
   * @param listener the listener to be added.
   */
  def addListener(listener: Controller): Unit

  /**
   * Notifies the View that the last inputted move was accepted.
   * @param gameState the game state after the last action was executed.
   */
  def moveAccepted(gameState: G): Unit

  /**
   * Notifies the View that the last inputted move was rejected.
   */
  def moveRejected(): Unit

  /**
   * Notifies the View that the last [[sbags.interaction.controller.Event]] was correctly received but a move is not detected yet, so more input is expected.
   */
  def nextCommand(): Unit

  /**
   * Starts displaying the game and capturing user input.
   * @param initialGameState the initial game state to be displayed.
   */
  def startGame(initialGameState: G): Unit

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
 * A view with a set of listeners.
 * @tparam G type of the game state.
 */
trait ListenedGameView[G] extends GameView[G] {
  protected var listenerSet: Set[Controller] = Set.empty
  override def addListener(listener: Controller): Unit = listenerSet += listener
}

