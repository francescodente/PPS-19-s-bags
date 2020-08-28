import examples.tictactoe.TicTacToe._

object TicTacToeSetup extends CliGameSetup[Move, State] with RectangularBoardSetup[BoardStructure, State] {
  override val gameDescription = TicTacToe

  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]) =
    rendering.withBoard.withTurns.withGameResult

  override def setupInputParser(builder: InputParserBuilder) = builder
    .addTileCommand()

  override def eventsToMove(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}