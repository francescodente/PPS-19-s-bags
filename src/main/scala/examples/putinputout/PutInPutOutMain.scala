package examples.putinputout

import sbags.interaction.view.cli.{BoardSetup, CliGameSetup, CliRenderer, InputParserBuilder}
import examples.putinputout.PutInPutOut._
import sbags.interaction.AppRunner
import sbags.interaction.view.{Event, RendererBuilder}
import sbags.model.core.GameDescription

object PutInPutOutMain extends App {
  AppRunner run PutInPutOutSetup
}

object PutInPutOutSetup extends CliGameSetup[Move, State] with BoardSetup[BoardStructure, State] {
  override val gameDescription: GameDescription[Move, State] = PutInPutOut

  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]): RendererBuilder[State, CliRenderer[State]] = rendering
    .addRenderer(BoardRenderer)

  object BoardRenderer extends CliRenderer[State] {
    override def render(state: State): Unit = {
      for (tile <- state.board.structure.tiles;
           pawn = state.board(tile);
           pawnDisplay = tileToString(pawn))
        println(s"$tile -> $pawnDisplay")
    }
  }

  case object In extends Event
  case object Out extends Event

  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case In :: Nil => Some(PutIn)
    case Out :: Nil => Some(PutOut)
    case _ => None
  }

  override def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder
    .addKeyword("in", In)
    .addKeyword("out", Out)
}
