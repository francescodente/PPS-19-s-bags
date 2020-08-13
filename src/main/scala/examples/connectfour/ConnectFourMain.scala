package examples.connectfour

import examples.connectfour.ConnectFour._
import sbags.interaction.view.cli.{CliBoardRenderer, CliGameResultRenderer, CliGameSetup, CliTurnRenderer, _}
import sbags.interaction.view.{Event, LaneSelected, RendererBuilder}

object ConnectFourMain extends CliGameSetup[Move, BoardStructure, State](ConnectFour) {
  override def pawnToString(pawn: BoardStructure#Pawn): String = pawn match {
    case Red => "R"
    case Blue => "B"
  }

  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case LaneSelected(x) :: Nil => Some(Put(x))
    case _ => None
  }

  override def setupRenderers(rendering: Rendering): Rendering = rendering
    .withBoard
    .withTurns
    .withGameResult
}
