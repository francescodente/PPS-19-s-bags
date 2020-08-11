package sbags.interaction.view

trait MenuView extends SubView[MenuViewListener] {
  def stopMenu(): Unit
}

trait MenuViewListener {
  def onStartGame(): Unit
  def onQuit(): Unit
}
