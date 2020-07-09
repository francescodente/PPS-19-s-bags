package examples.tictactoe

import sbags.entity.{BasicGameState, BasicRectangularBoard, GameDescription, RuleSet, TwoPlayersAlternativeTurn}

object TicTacToe extends GameDescription {
  type BoardState = TicTacToeBoard
  type GameState = TicTacToeState

  override def newGame: GameState = new TicTacToeState(new TicTacToeBoard)
}

class TicTacToeState(board: TicTacToeBoard) extends BasicGameState[TicTacToeBoard](board) with TwoPlayersAlternativeTurn[TicTacToePawn] {
  type Move = TicTacToeMove

  val tuplePlayer: (TicTacToePawn, TicTacToePawn) = (X, O)

  val ruleSet: RuleSet[TicTacToeMove, this.type] = new TicTacToeRuleSet
}

class TicTacToeRuleSet extends RuleSet[TicTacToeMove, TicTacToeState] {
  override def availableMoves(implicit state: TicTacToeState): Seq[TicTacToeMove] =
    for (t <- state.boardState.tiles; if state.boardState(t).isEmpty) yield Put(t)

  override def executeMove(move: TicTacToeMove)(implicit state: TicTacToeState): Unit = move match {
    case Put(tile) =>
      state.boardState << (state.currentTurn -> tile)
      state.nextTurn()
  }
}

class TicTacToeBoard extends BasicRectangularBoard(3, 3) {
  type Pawn = TicTacToePawn
}

sealed trait TicTacToePawn
case object X extends TicTacToePawn
case object O extends TicTacToePawn

sealed trait TicTacToeMove
case class Put(tile: (Int, Int)) extends TicTacToeMove
