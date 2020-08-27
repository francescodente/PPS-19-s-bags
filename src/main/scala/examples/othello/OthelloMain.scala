package examples.othello

import examples.othello.Othello._
import sbags.interaction.AppRunner
import sbags.interaction.view.cli._
import sbags.interaction.view.{Event, RendererBuilder, TileSelected}
import sbags.model.core.GameDescription

object OthelloMain extends App {
  AppRunner run OthelloSetup
}

object OthelloSetup extends CliGameSetup[Move, State] with RectangularBoardSetup[BoardStructure, State] {
  override val gameDescription: GameDescription[Move, State] = Othello

  /** The two pawns are represented by the letters B and W. */
  override def pawnToString(pawn: BoardStructure#Pawn): String = pawn match {
    case Black => "B"
    case White => "W"
  }

  /** The board's columns are displayed as letters, the rows as numbers starting from 1. */
  override def coordinateConverters: (Converter[Int], Converter[Int]) =
    (Converter.letters, Converter.oneBased)

  /** In Othello we select a tile to make a move, so no other command is required. */
  override def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder
    .addTileCommand()

  /** Enable the renderers for the used extensions. */
  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]): RendererBuilder[State, CliRenderer[State]] = rendering
    .withBoard
    .withTurns
    .withGameResult

  /** When a tile is selected we want the Put move to be recognized, otherwise no move should be found. */
  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
