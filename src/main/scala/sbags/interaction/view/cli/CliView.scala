package sbags.interaction.view.cli

import sbags.core.{BoardGameState, RectangularBoardStructure}
import sbags.interaction.controller.{Event, EventParser}
import sbags.interaction.view._

class CliView[B <: RectangularBoardStructure, G](override val renderers: Seq[CliRenderer[G]], parser: EventParser)
                                                (implicit ev: BoardGameState[B, G]) extends BasicView[G] {

  private val cliThread = new Thread(() => while (true) readCommand())

  override def moveRejected(): Unit = println("last move was illegal")

  override def moveAccepted(gameState: G): Unit = render(gameState)

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

  override def startGame(initialGameState: G): Unit = {
    render(initialGameState)
    println("write next command")
    cliThread.start()
  }
}

object CliView {
  def apply[B <: RectangularBoardStructure, G](renderers: Seq[CliRenderer[G]], parser: EventParser)(implicit ev: BoardGameState[B, G]): CliView[B, G] =
    new CliView(renderers, parser)
}
