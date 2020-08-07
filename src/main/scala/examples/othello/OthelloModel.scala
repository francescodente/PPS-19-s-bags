package examples.othello

import sbags.core.{Board, Coordinate, RectangularStructure}

sealed trait OthelloPawn
case object White extends OthelloPawn
case object Black extends OthelloPawn

sealed trait OthelloMove
case class Put(tile: Coordinate) extends OthelloMove

object OthelloBoard extends RectangularStructure(8, 8) {
  type Pawn = OthelloPawn
}

case class OthelloState(board: Board[OthelloBoard.type], currentPlayer: OthelloPawn)