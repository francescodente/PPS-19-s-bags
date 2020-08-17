package examples.tictactoe

import examples.tictactoe.TicTacToe._
import sbags.interaction.AppRunner
import sbags.interaction.view.cli._
import sbags.interaction.view.{Event, RendererBuilder, TileSelected}
import sbags.model.core.GameDescription

object TicTacToeMain extends App {
  AppRunner run TicTacToeSetup
}

object TicTacToeSetup extends CliGameSetup[Move, State] with RectangularBoardSetup[BoardStructure, State] {
  override val gameDescription: GameDescription[Move, State] = TicTacToe

  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]): RendererBuilder[State, CliRenderer[State]] = rendering
    .withBoard
    .withTurns
    .withGameResult

  override def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder
    .addTileCommand()

  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
