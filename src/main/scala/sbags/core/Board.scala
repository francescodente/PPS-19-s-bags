package sbags.core

import sbags.core.Board.{Empty, Loaded}

trait BoardStructure {
  /**
   * Defines the type of the tiles that compose the [[sbags.core.Board]].
   * For example, to declare a 2D board the type (Int, Int) can be assigned to this.
   */
  type Tile

  /**
   * Defines the type of the pawn that can be placed on the [[sbags.core.Board]].
   */
  type Pawn

  /**
   * Returns the sequence containing all the valid tile for this board.
   * @return the sequence containing all the valid tile for this board.
   */
  def tiles: Seq[Tile]
}

/**
 * Represents the definition of a generic Board, providing the basic functionality to work with it.
 * In particular, a Board is a set of logically structured Tiles.
 */
trait Board[B <: BoardStructure] {
  /**
   * Returns a [[scala.Option]] describing the pawn sitting at the given tile.
   * If the tile is empty [[scala.None]] is returned, otherwise the pawn is wrapped in a [[scala.Option]]
   * and returned.
   *
   * @param tile the tile to be queried.
   * @return Some(pawn) if the tile is occupied, None otherwise.
   */
  def apply(tile: B#Tile): Option[B#Pawn]

  /**
   * Sets the given pawn on the given tile, throwing an [[IllegalStateException]]
   * if the tile is not empty.
   *
   * @param pawn the pawn to be placed on the tile.
   * @param tile the tile where the pawn should be placed.
   * @return a [[sbags.core.Board]] with pawn into tile if the tile was empty.
   * @throws IllegalStateException if tile is not empty.
   */
  def place(pawn: B#Pawn, tile: B#Tile): Board[B]

  /**
   * Removes the pawn sitting on the given tile, throwing an [[IllegalStateException]] if the tile is empty.
   *
   * @param tile the tile to empty out.
   * @return a [[sbags.core.Board]] with the tile empty.
   * @throws IllegalStateException if tile is empty.
   */
  def clear(tile: B#Tile): Board[B]

  /**
   * Returns the board representation as a [[scala.collection.Map]].
   * @return the board representation as a [[scala.collection.Map]].
   */
  def boardMap: Map[B#Tile, B#Pawn]

  def structure: B
}

case class BasicBoard[B <: BoardStructure](boardMap: Map[B#Tile, B#Pawn], structure: B) extends Board[B] {
  override def apply(tile: B#Tile): Option[B#Pawn] = boardMap get tile

  private def ensureTileIsValid(tile: B#Tile): Unit =
    if (!structure.tiles.contains(tile))
      throw new IllegalArgumentException

  override def place(pawn: B#Pawn, tile: B#Tile): Board[B] = {
    ensureTileIsValid(tile)
    this (tile) match {
      case Some(_) => throw new IllegalStateException
      case None => BasicBoard(boardMap + (tile -> pawn), structure)
    }
  }

  override def clear(tile: B#Tile): Board[B] = {
    ensureTileIsValid(tile)
    this (tile) match {
      case Some(_) => BasicBoard(boardMap - tile, structure)
      case None => throw new IllegalStateException
    }
  }
}

trait BoardImplementation[B <: BoardStructure] extends Board[B] {
  override def apply(tile: B#Tile): Option[B#Pawn] = this match {
    case (p on `tile`) :: _ => Some(p)
    case _ :: b => b(tile)
    case _ => None
  }

  override def place(pawn: B#Pawn, tile: B#Tile): Board[B] = Loaded(pawn on tile, this)

  override def clear(tile: B#Tile): Board[B] = this match {
    case (_ on `tile`) :: b => b
    case _ :: b => b.clear(tile)
    case _ => throw new IllegalStateException
  }

  override def boardMap: Map[B#Tile, B#Pawn] = this match {
    case (p on t) :: b => b.boardMap + (t -> p)
    case _ => Map.empty
  }

  override def structure: B = this match {
    case Empty(s) => s
    case _ :: b => b.structure
  }
}

object :: {
  def unapply[B <: BoardStructure](board: Board[B]): Option[(PlacedPawn[B#Tile, B#Pawn], Board[B])] = board match {
    case Loaded(p, b) => Some((p, b))
    case _ => None
  }
}

object Board {
  def apply[B <: BoardStructure](structure: B): Board[B] =
    BasicBoard(Map.empty, structure)
  def apply[B <: BoardStructure](boardMap: Map[B#Tile, B#Pawn])(structure: B): Board[B] =
    BasicBoard(boardMap, structure)

  case class Empty[B <: BoardStructure](boardStructure: B)
    extends BoardImplementation[B]
  case class Loaded[B <: BoardStructure](placedPawn: PlacedPawn[B#Tile, B#Pawn], board: Board[B])
    extends BoardImplementation[B]
}


trait RectangularBoardStructure extends BoardStructure {
  type Tile = Coordinate
  val width: Int
  val height: Int

  override def tiles: Seq[Coordinate] = for (x <- 0 until width; y <- 0 until height) yield Coordinate(x, y)

}
/**
 * Represents a classic implementation for the [[sbags.core.Board]] trait.
 */
class RectangularBoard(val width: Int, val height: Int) extends RectangularBoardStructure

class SquareBoard(val size: Int) extends RectangularBoard(size, size)
