package examples.tictactoe

import sbags.core._

/**
 * Represents TicTacToe Pawns.
 */
sealed trait TicTacToePawn

object TicTacToePawn {
  def opponent(pawn: TicTacToePawn): TicTacToePawn = pawn match {
    case X => O
    case O => X
  }
}

/**
 * Represents the X in TicTacToe.
 */
case object X extends TicTacToePawn

/**
 * Represents the O in TicTacToe.
 */
case object O extends TicTacToePawn

/**
 * Represents the type of moves available in TicTacToe.
 */
sealed trait TicTacToeMove

/**
 * A move that puts X or O in the given position, based on the rule set of TicTacToe and the current turn.
 *
 * @param tile the tile that represents the position on the game board.
 */
case class Put(tile: Coordinate) extends TicTacToeMove

/**
 * Represents the TicTacToe board,
 * with size 3x3
 * and Pawn of type [[examples.tictactoe.TicTacToePawn]].
 */
object TicTacToeBoard extends SquareBoard(TicTacToe.size) {
  type Pawn = TicTacToePawn
}

case class TicTacToeState(board: Board[TicTacToe.BoardStructure], currentTurn: TicTacToePawn)
