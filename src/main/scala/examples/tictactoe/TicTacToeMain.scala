package examples.tictactoe

import sbags.interaction.controller.{Event, SequentialController, TileSelected}
import sbags.interaction.view.cli.{CliBoardRenderer, CliEventParser, CliGameResultRenderer, CliTurnRenderer, CliView}
import TicTacToe._

object TicTacToeMain extends App {

  private val renderers = Seq(
    CliBoardRenderer[BoardStructure, State](),
    new CliTurnRenderer[State],
    new CliGameResultRenderer[State]
  )

  private def ticTacToeMoves(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }

  private val view = CliView(renderers, CliEventParser())
  private val controller = new SequentialController(view, TicTacToe.newGame, ticTacToeMoves)

  view.addListener(controller)
  controller.startGame()
}
