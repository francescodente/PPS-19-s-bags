package examples.connectfour

import sbags.core._


sealed trait ConnectFourPawn
object ConnectFourPawn {
  def opponent(pawn: ConnectFourPawn): ConnectFourPawn = pawn match {
    case Red => Blue
    case Blue => Red
  }
}
case object Red extends ConnectFourPawn { override def toString: String = "R" }
case object Blue extends ConnectFourPawn{ override def toString: String = "B" }

sealed trait ConnectFourMove
case class Put(tile: Int) extends ConnectFourMove

object ConnectFourBoard extends RectangularBoard(ConnectFour.width, ConnectFour.height) {
  type Pawn = ConnectFourPawn
}

case class ConnectFourState(board: Board[ConnectFourBoard.type], currentTurn: ConnectFourPawn)
