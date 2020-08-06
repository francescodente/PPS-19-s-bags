package sbags.interaction.view

trait MenuView extends Startable with Observable[MenuViewListener]{
  def stopMenu(): Unit
}

trait MenuViewListener {
  def onStartGame(): Unit
  def onQuit(): Unit
}
