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

  override def pawnToString(pawn: BoardStructure#Pawn): String = pawn match {
    case Red => "⬤"
    case Blue => "◯"
  }

  override def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder
    .addColumnCommand()

  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case LaneSelected(x) :: Nil => Some(Put(x))
    case _ => None
  }

  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]): RendererBuilder[State, CliRenderer[State]] = rendering
    .withBoard
    .withTurns
    .withGameResult
}
