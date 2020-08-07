package examples.connectfour

import examples.connectfour.ConnectFour._
import sbags.interaction.view.cli.{CliBoardRenderer, CliGameResultRenderer, CliGameSetup, CliTurnRenderer, _}
import sbags.interaction.view.{Event, LaneSelected, RendererBuilder}

object ConnectFourMain extends CliGameSetup[Move, State](ConnectFour) {
  type Builder = RendererBuilder[State, CliRenderer[State]]

  private def pawnToString(pawn: Option[BoardStructure#Pawn]): String = pawn match {
    case Some(Red) => "R"
    case Some(Blue) => "B"
    case None => "_"
  }

  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case LaneSelected(x) :: Nil => Some(Put(x))
    case _ => None
  }

  override def setupRenderers(builder: Builder): Builder = builder
    .addRenderer(CliBoardRenderer(tileToString = pawnToString))
    .addRenderer(new CliTurnRenderer)
    .addRenderer(new CliGameResultRenderer)
}
