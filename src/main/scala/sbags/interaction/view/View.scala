package sbags.interaction.view

trait View[G] {
  def setupMenu(): MenuView
  def setupGame(initialGameState: G): GameView[G]
  def close(): Unit
}

trait Startable {
  def start(): Unit
}

trait Observable[L] {
  private var listenerSeq: Seq[L] = Seq.empty
  protected def notify(handler: L => Unit): Unit = listenerSeq.foreach(handler)
  /**
   * Adds a listener to be notified on input events.
   * @param listener the listener to be added.
   */
  def addListener(listener: L): Unit = listenerSeq = listenerSeq :+ listener

  def clearListeners(): Unit = listenerSeq = Seq.empty
}
