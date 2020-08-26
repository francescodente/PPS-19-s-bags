package examples.connectfour

import sbags.model.core.{Board, RectangularStructure}

/** Represents Connect Four Pawns. */
sealed trait ConnectFourPawn

object ConnectFourPawn {
  def opponent(pawn: ConnectFourPawn): ConnectFourPawn = pawn match {
    case Red => Blue
    case Blue => Red
  }
}

/** Represents the Red in Connect Four. */
case object Red extends ConnectFourPawn

/** Represents the Blue in Connect Four. */
case object Blue extends ConnectFourPawn

/** Represents the type of moves available in Connect Four. */
sealed trait ConnectFourMove

/**
 * A move that puts Red or Blue (based on the current turn) in the given column at the first available position.
 *
 * @param tile the tile that represents the position on the game board.
 */
case class Put(tile: Int) extends ConnectFourMove

/**
 * Represents the Connect Four board, with size 6x7 and pawns of type [[examples.connectfour.ConnectFourPawn]]
 */
object ConnectFourBoard extends RectangularStructure(ConnectFour.width, ConnectFour.height) {
  type Pawn = ConnectFourPawn
}

/**
 * Represents the Connect Four state,
 * comprising of a board and the current player.
 */
case class ConnectFourState(board: Board[ConnectFour.BoardStructure], currentPlayer: ConnectFourPawn)
