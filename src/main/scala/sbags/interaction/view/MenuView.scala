package sbags.interaction.view

/** The GUI displaying the Menu. */
trait MenuView extends SubView[MenuViewListener] {

  /** Terminates the execution of the View. */
  def stopMenu(): Unit
}

/** The Listener responding to events emitted by [[MenuView]] */
trait MenuViewListener {

  /** Does something when the game starts. */
  def onStartGame(): Unit
  /** Does something when the application is quit. */
  def onQuit(): Unit
}
