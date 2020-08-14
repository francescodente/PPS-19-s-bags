package examples.tictactoe

import examples.tictactoe.TicTacToe._
import sbags.interaction.view.cli._
import sbags.interaction.view.{Event, RendererBuilder, TileSelected}

object TicTacToeMain extends CliGameSetup[Move, State] with RectangularBoardSetup[BoardStructure, State] {
  override val gameDescription = TicTacToe

  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]) = rendering
    .withBoard
    .withTurns
    .withGameResult

  override def eventsToMove(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
