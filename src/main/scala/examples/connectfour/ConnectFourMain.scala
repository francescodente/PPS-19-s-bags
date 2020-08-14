package examples.connectfour

import examples.connectfour.ConnectFour._
import sbags.interaction.view.cli._
import sbags.interaction.view.{Event, LaneSelected, RendererBuilder}

object ConnectFourMain extends CliGameSetup[Move, State] with RectangularBoardSetup[BoardStructure, State] {
  override val gameDescription = ConnectFour

  override def pawnToString(pawn: BoardStructure#Pawn) = pawn match {
    case Red => "R"
    case Blue => "B"
  }

  override def eventsToMove(events: List[Event]) = events match {
    case LaneSelected(x) :: Nil => Some(Put(x))
    case _ => None
  }

  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]) = rendering
    .withBoard
    .withTurns
    .withGameResult
}
