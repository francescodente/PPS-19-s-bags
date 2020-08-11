package sbags.interaction.view

/**
 * Represents a generic View divided in Menu and Game visual.
 * @tparam G type of the game state.
 */
trait View[G] {
  def setupMenu(): MenuView
  def setupGame(initialGameState: G): GameView[G]
  def close(): Unit
}

/**
 * Represents a Startable object.
 */
trait Startable {
  /**
   * starts the object to have his behaviour.
   */
  def start(): Unit
}

/**
 * Represents an observable object that can notify his listeners.
 * @tparam L type of listeners.
 */
trait Observable[L] {
  private var listenerSeq: Seq[L] = Seq.empty
  protected def notify(handler: L => Unit): Unit = listenerSeq.foreach(handler)
  /**
   * Adds a listener to be notified on input events.
   * @param listener the listener to be added.
   */
  def addListener(listener: L): Unit = listenerSeq = listenerSeq :+ listener

  /**
   * Clears the Seq of listeners to be notified on input events.
   */
  def clearListeners(): Unit = listenerSeq = Seq.empty
}

trait SubView[L] extends Startable with Observable[L]
