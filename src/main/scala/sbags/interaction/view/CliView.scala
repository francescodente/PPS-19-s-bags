package sbags.interaction.view

import sbags.core.BoardGameState._
import sbags.core.{BoardGameState, RectangularBoardStructure}
import sbags.interaction.controller.{Event, EventParser}

class CliView[B <: RectangularBoardStructure, G](xModifier: Int => String,
                                                 yModifier: Int => String,
                                                 parser: EventParser)
                                                (implicit ev: BoardGameState[B, G])

  extends BasicView[G] {

  private val stringifier = Stringifier[B](xModifier, yModifier)

  override def moveAccepted(gameState: G): Unit = write(stringifier.buildBoard(gameState.boardState))

  override def moveRejected(): Unit = write("last move was illegal")

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
  def apply[B <: RectangularBoardStructure, G](xyModifier: Int => String = _ + 1 + "")(parser: EventParser)(implicit ev: BoardGameState[B, G]): CliView[B, G] =
    apply(xyModifier, xyModifier, parser)

  def apply[B <: RectangularBoardStructure, G](xModifier: Int => String, yModifier: Int => String, parser: EventParser)(implicit ev: BoardGameState[B, G]): CliView[B, G] =
    new CliView(xModifier, yModifier, parser)
}
