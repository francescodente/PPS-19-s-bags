package examples.tictactoe

import sbags.interaction.controller.{Event, SequentialController, TileSelected}
import sbags.interaction.view.cli.{CliBoardRenderer, CliEventParser, CliGameResultRenderer, CliTurnRenderer, CliGameView}
import TicTacToe._

object TicTacToeMain extends App {

  private val renderers = Seq(
    CliBoardRenderer[BoardStructure, State](),
    new CliTurnRenderer[State],
    new CliGameResultRenderer[State]
  )

  private val ticTacToeMoves: List[Event] => Option[Move] = {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }

  private val view = CliGameView(renderers, CliEventParser())
  private val controller = new SequentialController(view, newGame, ticTacToeMoves)

  view.addListener(controller)
  controller.startGame()
}
