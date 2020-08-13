package examples.tictactoe

import examples.tictactoe.TicTacToe._
import sbags.interaction.view.cli._
import sbags.interaction.view.{Event, TileSelected}

object TicTacToeMain extends CliGameSetup[Move, BoardStructure, State](TicTacToe) {
  override def setupRenderers(rendering: Rendering): Rendering =
    rendering
      .withBoard
      .withTurns
      .withGameResult

  override def eventsToMove(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
