package sbags.interaction.view.cli

import sbags.interaction.view.{MenuView, MenuViewListener}

class CliMenuView extends MenuView {
  case class MenuAction(name: String, handler: MenuViewListener => Unit)
  private val menuOptions: Seq[MenuAction] = Seq(
    MenuAction("start game", _.onStartGame()),
    MenuAction("quit", _.onQuit())
  )

  override def start(): Unit = {
    println("welcome to the game menu")
    loop
  }

  private def loop: IO[Unit] = {
    for {
      _ <- printOptions()
      x <- read()
      res = menuOptions(x.toInt)
      _ <- write(res.name)
      _ = notify(res.handler)
    } yield()
  }

  private def printOptions(): IO[String] =
    write(menuOptions
      .zipWithIndex
      .map(t => s"${t._2} - ${t._1}")
      .foldLeft("")(_ + "\n" + _)
    )
}

