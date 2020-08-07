package examples.tictactoe

import examples.tictactoe.TicTacToe._
import sbags.interaction.view.cli._
import sbags.interaction.view.{Event, RendererBuilder, TileSelected}

object TicTacToeMain extends CliGameSetup[Move, State](TicTacToe) {
  override def setupRenderers(builder: RendererBuilder[TicTacToe.State, CliRenderer[TicTacToe.State]]) = builder
    .addRenderer(CliBoardRenderer[BoardStructure, State]())
    .addRenderer(new CliTurnRenderer[State])
    .addRenderer(new CliGameResultRenderer[State])

  override def eventsToMove(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
