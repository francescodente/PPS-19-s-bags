package examples.connectfour

import sbags.core._

sealed trait ConnectFourPawn
object ConnectFourPawn {
  def opponent(pawn: ConnectFourPawn): ConnectFourPawn = pawn match {
    case Red => Blue
    case Blue => Red
  }
}
case object Red extends ConnectFourPawn
case object Blue extends ConnectFourPawn

sealed trait ConnectFourMove
case class Put(tile: Int) extends ConnectFourMove

object ConnectFourBoard extends RectangularStructure(ConnectFour.width, ConnectFour.height) {
  type Pawn = ConnectFourPawn
}

case class ConnectFourState(board: Board[ConnectFour.BoardStructure], players: Seq[ConnectFourPawn])
