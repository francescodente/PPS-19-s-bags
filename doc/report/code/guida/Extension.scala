object TicTacToe extends GameDescription[TicTacToeMove, TicTacToeState] {
  /* ... */
  private val players: Seq[TicTacToePawn] = Seq(X, O)
  /* ... */
  implicit lazy val turns: PlayersAsTurns[BoardStructure#Pawn, State] =
    PlayersAsTurns.roundRobin(_ => players, (s,p) => s.copy(currentPlayer = p))
  /* ... */
}