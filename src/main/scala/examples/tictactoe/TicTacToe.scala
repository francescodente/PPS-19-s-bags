package examples.tictactoe

import sbags.entity.{BasicGameState, BasicRectangularBoard, GameDescription}

object TicTacToe extends GameDescription {
  type BoardState = TicTacToeBoard
  type GameState = TicTacToeState

  override def newGame: GameState = new TicTacToeState(new TicTacToeBoard)
}

class TicTacToeState(board: TicTacToeBoard) extends BasicGameState[TicTacToeBoard](board) {
  type Move = TicTacToeMove

  private var turn: TicTacToePawn = X

  private def opposite(pawn: TicTacToePawn): TicTacToePawn = pawn match {
    case X => O
    case O => X
  }

  override def executeMove(move: TicTacToeMove): Unit = move match {
    case Put(tile) =>
      boardState << (turn -> tile)
      turn = opposite(turn)
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
