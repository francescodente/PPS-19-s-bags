package sbags.interaction.view

import sbags.core.{BoardGameState, RectangularBoardStructure}
import sbags.interaction.controller.{Event, EventParser}

class CliView[B <: RectangularBoardStructure, G](override val renderers: Seq[CliRenderer[G]], parser: EventParser)
                                                (implicit ev: BoardGameState[B, G]) extends BasicView[G] {

  private val cliThread = new Thread(() => while (true) readCommand())

  override def moveRejected(): Unit = println("last move was illegal")

  override def nextCommand(): Unit = println("write next command: ")

  private def readCommand(): IO[Unit] =
    for {
      input <- read()
      event = parser.parse(input).map(notify)
      _ <- write(s"Last inputAction was ${if (event.isDefined) "defined" else "undefined"}")
    } yield()

  private def notify(event: Event): IO[_] = {
    println("calling notify")
    unit(listenerSet.foreach(_.notify(event)))
  }

  override def startGame(): Unit = {
    println("write next command")
    cliThread.start()
  }
}

object CliView {
  def apply[B <: RectangularBoardStructure, G](renderers: Seq[CliRenderer[G]], parser: EventParser)(implicit ev: BoardGameState[B, G]): CliView[B, G] =
    new CliView(renderers, parser)
}
