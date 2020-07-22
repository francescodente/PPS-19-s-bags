package examples.tictactoe

import sbags.interaction.controller.{DefaultEventParser, Event, SequentialController, TileSelected}
import sbags.interaction.view.cli.{CliBoardRenderer, CliGameResultRenderer, CliTurnRenderer, CliView}


object TicTacToeMain extends App {
  import TicTacToe._
  private val renderers = Seq(CliBoardRenderer(), new CliTurnRenderer[TicTacToe.State], new CliGameResultRenderer[TicTacToe.State])

  private def ticTacToeMoves(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
  private val view = CliView(renderers, DefaultEventParser())
  private val controller = new SequentialController[TicTacToe.State,TicTacToe.Move](view, TicTacToe.newGame, ticTacToeMoves)

  view.addListener(controller)
  controller.startGame()
}
