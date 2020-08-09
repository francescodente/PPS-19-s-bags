package examples.othello

import examples.othello.Othello._
import sbags.interaction.view.cli.{CliBoardRenderer, CliGameSetup, CliRenderer, CliTurnRenderer}
import sbags.interaction.view.{Event, RendererBuilder, TileSelected}

object OthelloMain extends CliGameSetup[Move, State](Othello) {
  private def pawnToString(pawn: Option[BoardStructure#Pawn]): String = pawn match {
    case Some(Black) => "⬤"
    case Some(White) => "◯"
    case _ => "_"
  }

  override def setupRenderers(builder: RendererBuilder[State, CliRenderer[State]]) = builder
    .addRenderer(CliBoardRenderer(xModifier = x => ('a' + x).toChar.toString, tileToString = pawnToString))
    .addRenderer(new CliTurnRenderer)

  override def eventsToMove(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
