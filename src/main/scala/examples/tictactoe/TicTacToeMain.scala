package examples.tictactoe

import sbags.interaction.controller.{DefaultEventParser, Event, SequentialInputListener, TileSelected}
import sbags.interaction.view.{CliBoardRenderer, CliGameResultRenderer, CliTurnRenderer, CliView}


object TicTacToeMain extends App {
  import TicTacToe._
  private val renderers = Seq(CliBoardRenderer(), new CliTurnRenderer[TicTacToe.State], new CliGameResultRenderer[TicTacToe.State])

  private def ticTacToeMoves(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
  private val view = CliView(renderers, DefaultEventParser())

  view.addListener(new SequentialInputListener[TicTacToe.State,TicTacToe.Move](view, TicTacToe.newGame, ticTacToeMoves))
  view.startGame()
}
