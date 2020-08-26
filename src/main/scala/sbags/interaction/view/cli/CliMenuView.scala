package sbags.interaction.view.cli

import sbags.interaction.view.{MenuView, MenuViewHandler}

import scala.io.StdIn

/** The CLI displaying the Menu. */
class CliMenuView extends MenuView {
  private var menuEnded = false

  private case class MenuAction(name: String, handler: MenuViewHandler => Unit)
  private val menuOptions: Seq[MenuAction] = Seq(
    MenuAction("start game", _.onStartGame()),
    MenuAction("Quit", _.onQuit())
  )

  override def stopMenu(): Unit = menuEnded = true

  override def start(): Unit = {
    menuEnded = false
    println("welcome to the game menu")
    while (!menuEnded) loop()
  }

  private def loop(): Unit = {
    printMenuOptions()
    val x = StdIn.readLine()
    val res = if (x.matches("^\\d+$")) menuOptions(x.toInt) else menuOptions.last
    println(res.name)
    handle(res.handler)
  }

  private def printMenuOptions(): Unit =
    menuOptions
      .zipWithIndex
      .map(t => s"${t._2} - ${t._1.name}")
      .foreach(println)

}

