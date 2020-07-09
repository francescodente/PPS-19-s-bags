package examples.tictactoe

import sbags.entity.{BasicGameState, BasicRectangularBoard, GameDescription, RuleSet, Turns}

object TicTacToe extends GameDescription {
  type BoardState = TicTacToeBoard
  type GameState = TicTacToeState

  override def newGame: GameState = new TicTacToeState(new TicTacToeBoard)
}

class TicTacToeState(board: TicTacToeBoard) extends BasicGameState[TicTacToeBoard](board) {
  type Move = TicTacToeMove

  var turn: TicTacToePawn = X

  def opposite(pawn: TicTacToePawn): TicTacToePawn = pawn match {
    case X => O
    case O => X
  }

  val ruleSet: RuleSet[TicTacToeMove, this.type] = new TicTacToeRuleSet
}

class TicTacToeRuleSet extends RuleSet[TicTacToeMove, TicTacToeState] {
  override def availableMoves(implicit state: TicTacToeState): Seq[TicTacToeMove] =
    for (t <- state.boardState.tiles; if state.boardState(t).isEmpty) yield Put(t)

  override def executeMove(move: TicTacToeMove)(implicit state: TicTacToeState): Unit = move match {
    case Put(tile) =>
      state.boardState << (state.turn -> tile)
      state.turn = state.opposite(state.turn)
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
