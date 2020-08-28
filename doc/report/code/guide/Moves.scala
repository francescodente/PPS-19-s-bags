sealed trait TicTacToeMove

case class Put(tile: Coordinate) extends TicTacToeMove
