package sbags.interaction.view

/** The GUI displaying the Menu. */
trait MenuView extends SubView[MenuViewHandler]

/** Represents the set of handler responding to events emitted by [[MenuView]]*/
trait MenuViewHandler {

  /** Does something when the game starts. */
  def onStartGame(): Unit

  /** Does something when the application is quit. */
  def onQuit(): Unit
}
