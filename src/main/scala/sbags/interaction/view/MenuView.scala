package sbags.interaction.view

trait MenuView extends Startable with Observable[MenuViewListener]

trait MenuViewListener {
  def onStartGame(): Unit
  def onQuit(): Unit
}
