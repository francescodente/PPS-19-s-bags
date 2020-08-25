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

  /** Enable the renderers for the used extensions. */
  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]): RendererBuilder[State, CliRenderer[State]] = rendering
    .withBoard
    .withTurns
    .withGameResult

  /** In TicTacToe we just select a tile to make a move, so no other command is required. */
  override def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder
    .addTileCommand()

  /** When a tile is selected we want the Put move to be recognized, otherwise no move should be found. */
  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
