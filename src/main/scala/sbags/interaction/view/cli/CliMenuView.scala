package sbags.interaction.view.cli

import sbags.interaction.view.{MenuView, MenuViewHandler}

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
    while (!menuEnded) loop
  }

  private def loop: IO[Unit] = {
    for {
      _ <- printOptions()
      x <- read()
      res = if (x.matches("^\\d+$")) menuOptions(x.toInt) else menuOptions.last
      _ <- write(res.name)
      _ = handle(res.handler)
    } yield()
  }

  private def printOptions(): IO[String] =
    write(menuOptions
      .zipWithIndex
      .map(t => s"${t._2} - ${t._1.name}")
      .foldLeft("")(_ + "\n" + _)
    )

}

