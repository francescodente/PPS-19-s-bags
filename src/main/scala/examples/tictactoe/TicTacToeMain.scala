package examples.tictactoe

import sbags.interaction.controller.SequentialGameController
import sbags.interaction.view.cli.{CliEventParser, CliGameView}
import TicTacToe._
import sbags.interaction.view.{Event, TileSelected}
import sbags.interaction.view.cli.{CliBoardRenderer, CliGameResultRenderer, CliTurnRenderer}

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

  private val view = CliGameView(renderers, CliEventParser(), newGame.currentState)
//  private val controller = new SequentialGameController(view, newGame, ticTacToeMoves)
//
//  view.addListener(controller)
}
