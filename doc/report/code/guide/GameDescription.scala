object TicTacToe extends GameDescription[TicTacToeMove, TicTacToeState] {
  override def initialState: TicTacToeState =
    TicTacToeState(Board(TicTacToeBoard), X)

  override val ruleSet: RuleSet[TicTacToeMove, TicTacToeState] =
    ??? // Added later
}