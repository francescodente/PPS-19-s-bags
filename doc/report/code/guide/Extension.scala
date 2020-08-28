object TicTacToe extends GameDescription[TicTacToeMove, TicTacToeState] {
  ...
  type BoardStructure = TicTacToeBoard.type

  private val players: Seq[TicTacToePawn] = Seq(X, O)

  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((s, b) => s.copy(board = b))

  implicit lazy val turns: PlayersAsTurns[BoardStructure#Pawn, State] =
    PlayersAsTurns.roundRobin(_ => players, (s,p) => s.copy(currentPlayer = p))
  
  implicit lazy val endCondition: WinOrDrawCondition[BoardStructure#Pawn, State] =
    WinOrDrawCondition(state => ...)
  ...
}