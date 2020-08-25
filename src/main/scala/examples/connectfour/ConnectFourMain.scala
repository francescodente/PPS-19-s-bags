package examples.connectfour

import examples.connectfour.ConnectFour._
import sbags.interaction.AppRunner
import sbags.interaction.view.cli._
import sbags.interaction.view.{Event, LaneSelected, RendererBuilder}
import sbags.model.core.GameDescription

object ConnectFourMain extends App {
  AppRunner run ConnectFourSetup
}

object ConnectFourSetup extends CliGameSetup[Move, State] with RectangularBoardSetup[BoardStructure, State] {
  override val gameDescription: GameDescription[Move, State] = ConnectFour

  /** The two pawns are represented by the letters R and B. */
  override def pawnToString(pawn: BoardStructure#Pawn): String = pawn match {
    case Red => "R"
    case Blue => "B"
  }

  /** In Connect Four we just select a column to make a move, so no other command is required. */
  override def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder
    .addColumnCommand()

  /** When a lane is selected we want the Put move to be recognized, otherwise no move should be found. */
  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case LaneSelected(x) :: Nil => Some(Put(x))
    case _ => None
  }

  /** Enable the renderers for the used extensions. */
  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]): RendererBuilder[State, CliRenderer[State]] = rendering
    .withBoard
    .withTurns
    .withGameResult
}
