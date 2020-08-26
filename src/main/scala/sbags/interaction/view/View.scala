package sbags.interaction.view

/**
 * Represents a generic View divided in Menu and Game visual.
 *
 * @tparam G type of the game state.
 */
trait View[G] {
  /** Prepares for use and returns a [[sbags.interaction.view.MenuView]]. */
  def setupMenu(): MenuView

  /**
   * Prepares for use and returns a [[sbags.interaction.view.GameView]].
   *
   * @param initialGameState the first game state to be displayed.
   * @return a [[sbags.interaction.view.GameView]].
   */
  def setupGame(initialGameState: G): GameView[G]

  /** Terminates the current application. */
  def close(): Unit
}

/** Represents a Startable object. */
trait Startable {
  /** Starts the object to have his behaviour. */
  def start(): Unit
}

/**
 * Represents an observable object that can act using the handler registered from each listener.
 *
 * @tparam L type of listeners.
 */
trait Observable[L] {
  private var listenerSeq: Seq[L] = Seq.empty

  /**
   * Adds a listener to be notified on input events.
   *
   * @param listener the listener to be added.
   */
  def addListener(listener: L): Unit = listenerSeq = listenerSeq :+ listener

  /** Clears the Seq of listeners to be notified on input events. */
  def clearListeners(): Unit = listenerSeq = Seq.empty

  /**
   * Execute, in order, the handler of each listener.
   *
   * @param handler the function called for each listener.
   */
  protected def handle(handler: L => Unit): Unit = listenerSeq.foreach(handler)
}

/**
 * A view that is dependent on [[sbags.interaction.view.View]].
 * It is [[sbags.interaction.view.Startable]] and [[sbags.interaction.view.Observable]].
 *
 * @tparam L type of listeners.
 */
trait SubView[L] extends Startable with Observable[L]
