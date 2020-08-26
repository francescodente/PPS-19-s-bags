package sbags.interaction.view.cli

import sbags.interaction.view.{Event, _}
import sbags.model.core.{Error, Failure, InvalidMove}

import scala.io.StdIn

/**
 * Represents a view that displays the game and takes user input through the command line.
 *
 * @param renderers the [[sbags.interaction.view.Renderer]]s that this view will use to display the game.
 * @param parser a mapping from the strings typed by the user into [[Event]]s.
 * @param initialGameState the initial state to display at the start.
 * @tparam G type of the game state.
 */
class CliGameView[G](override val renderers: Seq[CliRenderer[G]],
                     parser: String => Option[Event],
                     initialGameState: G) extends GameView[G] {

  private var gameEnded = false

  override def moveRejected(failure: Failure): Unit = failure match {
    case InvalidMove => println("last move was illegal")
    case Error(t) => println(s"last move failed with error: ${t.getMessage}")
  }

  override def moveAccepted(gameState: G): Unit = render(gameState)

  private def readCommand(): Unit = {
    val input = StdIn.readLine()
    val event = parser(input)
    event.foreach(e => handle(_.onEvent(e)))
    println(s"last inputAction was ${if (event.isDefined) "accepted" else "undefined"}")
  }

  override def start(): Unit = {
    gameEnded = false
    render(initialGameState)
    println("command: ")
    while (!gameEnded) readCommand()
  }

  override def stopGame(): Unit = gameEnded = true
}

/** Factory for [[sbags.interaction.view.cli.CliGameView]] instances. */
object CliGameView {
  /**
   * Creates a CliGameView initialized with the following params.
   *
   * @param renderers the [[sbags.interaction.view.Renderer]]s that this view will use to display the game.
   * @param parser a mapping from the strings typed by the user into [[Event]]s.
   * @param initialGameState the initial state to display at the start.
   * @tparam G type of the game state.
   * @return the new CliGameView created.
   */
  def apply[G](renderers: Seq[CliRenderer[G]], parser: String => Option[Event], initialGameState: G): CliGameView[G] =
    new CliGameView(renderers, parser, initialGameState)
}
