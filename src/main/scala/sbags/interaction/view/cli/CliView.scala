package sbags.interaction.view.cli

import sbags.core.{BoardGameState, RectangularBoardStructure}
import sbags.interaction.controller.{Event, EventParser}
import sbags.interaction.view._

class CliView[B <: RectangularBoardStructure, G](override val renderers: Seq[CliRenderer[G]], parser: EventParser)
                                                (implicit ev: BoardGameState[B, G]) extends BasicView[G] {

  override def moveRejected(): Unit = println("last move was illegal")

  override def nextCommand(): Unit = readCommand()

  private def readCommand(): IO[Unit] =
    for {
      _ <- write("Need a command: ")
      input <- read()
      event: Event = parser.parse(input).head
      _ <- notify(event)
      _ <- write(s"InputAction: $event")
    } yield()

  private def notify(event: Event): IO[_] = unit(listenerSet.foreach(_.notify(event)))
}

object CliView {
  def apply[B <: RectangularBoardStructure, G](renderers: Seq[CliRenderer[G]], parser: EventParser)(implicit ev: BoardGameState[B, G]): CliView[B, G] =
    new CliView(renderers, parser)
}
