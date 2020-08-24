override def initialState: State = ConnectFourState(Board(ConnectFourBoard), Red)

override val ruleSet: RuleSet[Move, State] = ConnectFourRuleSet

implicit lazy val boardState: BoardState[BoardStructure, State] =
  BoardState((s, b) => s.copy(board = b))

private val players: Seq[BoardStructure#Pawn] = Seq(Red, Blue)

implicit lazy val turns: PlayersAsTurns[BoardStructure#Pawn, State] =
  PlayersAsTurns.roundRobin(_ => players, (s, p) => s.copy(currentPlayer = p))

implicit lazy val endCondition: WinOrDrawCondition[BoardStructure#Pawn, State] = ...