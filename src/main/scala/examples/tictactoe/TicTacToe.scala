package examples.tictactoe

import sbags.entity.{BasicGameState, BasicRectangularBoard, EndTurnAfterEachMove, GameDescription, GameEndCondition, RuleSet, TwoPlayersAlternateTurn}

object TicTacToe extends GameDescription {
  type BoardState = TicTacToeBoard
  type GameState = TicTacToeState

  override def newGame: GameState = new TicTacToeState(new TicTacToeBoard)
}

class TicTacToeState(board: TicTacToeBoard)
  extends BasicGameState[TicTacToeBoard](board)
    with TwoPlayersAlternateTurn[TicTacToePawn]
    with EndTurnAfterEachMove[TicTacToeBoard]
    with GameEndCondition[TicTacToeBoard] {
  type Move = TicTacToeMove

  val tuplePlayer: (TicTacToePawn, TicTacToePawn) = (X, O)

  val ruleSet: RuleSet[TicTacToeMove, this.type] = new TicTacToeRuleSet

  override type Result = TicTacToeResult

  override def gameResult: Option[TicTacToeResult] = None // TODO
}

class TicTacToeRuleSet extends RuleSet[TicTacToeMove, TicTacToeState] {
  override def availableMoves(implicit state: TicTacToeState): Seq[TicTacToeMove] =
    for (t <- state.boardState.tiles; if state.boardState(t).isEmpty) yield Put(t)

  override def executeMove(move: TicTacToeMove)(implicit state: TicTacToeState): Unit = move match {
    case Put(tile) =>
      state.boardState << (state.currentTurn -> tile)
  }
}

class TicTacToeBoard extends BasicRectangularBoard(3, 3) {
  type Pawn = TicTacToePawn
}

sealed trait TicTacToeResult
case object Draw extends TicTacToeResult
case class Winner(pawn: TicTacToePawn) extends TicTacToeResult

sealed trait TicTacToePawn
case object X extends TicTacToePawn
case object O extends TicTacToePawn

sealed trait TicTacToeMove
case class Put(tile: (Int, Int)) extends TicTacToeMove
