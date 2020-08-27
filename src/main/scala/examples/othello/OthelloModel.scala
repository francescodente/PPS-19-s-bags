package examples.othello

import sbags.model.core.{Board, Coordinate, RectangularStructure}

/** Represents Othello Pawns. */
sealed trait OthelloPawn
/** Represents the White in Othello */
case object White extends OthelloPawn
/** Represents the Black in Othello */
case object Black extends OthelloPawn

/** Represents the type of moves available in Othello. */
sealed trait OthelloMove

/**
 * A move that puts White or Black (based on the current turn) in the given tile.
 *
 * @param tile the tile that represents the position on the game board.
 */
case class Put(tile: Coordinate) extends OthelloMove

/** Represents the Othello board, with size 8x8 and pawns of type [[examples.othello.OthelloPawn]] */
object OthelloBoard extends RectangularStructure(8, 8) {
  type Pawn = OthelloPawn
}

/**
 * Represents the Othello state,
 * comprising of a board and the current player.
 */
case class OthelloState(board: Board[Othello.BoardStructure], currentPlayer: OthelloPawn)