package examples.othello

import examples.othello.Othello._
import sbags.interaction.AppRunner
import sbags.interaction.view.cli.{CliGameSetup, CliRenderer, Converter, Converters, RectangularBoardSetup}
import sbags.interaction.view.{Event, RendererBuilder, TileSelected}
import sbags.model.core.GameDescription

object OthelloMain extends App {
  AppRunner run OthelloSetup
}

object OthelloSetup extends CliGameSetup[Move, State] with RectangularBoardSetup[BoardStructure, State] {
  override val gameDescription: GameDescription[Move, State] = Othello

  override def pawnToString(pawn: BoardStructure#Pawn): String = pawn match {
    case Black => "⬤"
    case White => "◯"
  }

  override def coordinateConverters: (Converter[Int], Converter[Int]) =
    (Converters.letters, Converters.oneBased)

  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]): RendererBuilder[State, CliRenderer[State]] = rendering
    .withBoard
    .withTurns

  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
