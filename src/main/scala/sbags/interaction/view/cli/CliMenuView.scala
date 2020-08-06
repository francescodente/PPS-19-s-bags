package sbags.interaction.view.cli

import sbags.interaction.view.{MenuView, MenuViewListener}

class CliMenuView extends MenuView {
  private var menuEnded = false

  case class MenuAction(name: String, handler: MenuViewListener => Unit)
  private val menuOptions: Seq[MenuAction] = Seq(
    MenuAction("start game", _.onStartGame()),
    MenuAction("Quit", _.onQuit())
  )

  override def stopMenu(): Unit = menuEnded = true

  override def start(): Unit = {
    menuEnded = false
    println("welcome to the game menu")
    while (!menuEnded) loop
  }

  private def loop: IO[Unit] = {
    for {
      _ <- printOptions()
      x <- read()
      res = if (x.matches("^\\d+$")) menuOptions(x.toInt) else menuOptions.last
      _ <- write(res.name)
      _ = notify(res.handler)
    } yield()
  }

  private def printOptions(): IO[String] =
    write(menuOptions
      .zipWithIndex
      .map(t => s"${t._2} - ${t._1.name}")
      .foldLeft("")(_ + "\n" + _)
    )

}

