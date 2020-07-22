package sbags.interaction.view.cli

import sbags.core.{BoardGameState, RectangularBoardStructure}
import sbags.interaction.controller.Event
import sbags.interaction.view._

/**
 * A view that displays the game and takes user input through the command line.
 * @param renderers the [[sbags.interaction.view.Renderer]]s that this view will use to display the game.
 * @param parser a [[sbags.interaction.view.cli.CliEventParser]] mapping the strings typed by the user into [[sbags.interaction.controller.Event]]s.
 * @param ev the board game state.
 * @tparam B type of the board structure, with [[sbags.core.RectangularBoardStructure]] as an upper bound.
 * @tparam G type of the game state.
 */
class CliView[B <: RectangularBoardStructure, G](override val renderers: Seq[CliRenderer[G]], parser: CliEventParser)
                                                (implicit ev: BoardGameState[B, G]) extends ListenedView[G] {

  private var gameEnded = false

  private val cliThread = new Thread(() => while (!gameEnded) readCommand())

  override def moveRejected(): Unit = println("last move was illegal")

  override def moveAccepted(gameState: G): Unit = render(gameState)

  override def nextCommand(): Unit = println("next command: ")

  private def readCommand(): IO[Unit] =
    for {
      input <- read()
      event = parser.parse(input).map(notify)
      _ <- write(s"last inputAction was ${if (event.isDefined) "accepted" else "undefined"}")
    } yield()

  private def notify(event: Event): IO[_] = {
    unit(listenerSet.foreach(_.notify(event)))
  }

  override def startGame(initialGameState: G): Unit = {
    render(initialGameState)
    println("command: ")
    cliThread.start()
  }

  override def shutDown(): Unit = gameEnded = true
}

object CliView {
  def apply[B <: RectangularBoardStructure, G](renderers: Seq[CliRenderer[G]], parser: CliEventParser)(implicit ev: BoardGameState[B, G]): CliView[B, G] =
    new CliView(renderers, parser)
}
