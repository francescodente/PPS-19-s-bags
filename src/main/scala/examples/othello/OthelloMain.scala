package examples.othello

import examples.othello.Othello._
import sbags.interaction.view.cli.CliGameSetup
import sbags.interaction.view.{Event, TileSelected}

object OthelloMain extends CliGameSetup[Move, BoardStructure, State](Othello) {
  override def pawnToString(pawn: BoardStructure#Pawn): String = pawn match {
    case Black => "⬤"
    case White => "◯"
  }

  override def setupRenderers(rendering: Rendering): Rendering = rendering
    .withBoard
    .withTurns

  override def eventsToMove(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
