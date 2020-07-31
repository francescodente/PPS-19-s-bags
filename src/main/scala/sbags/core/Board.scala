package sbags.core

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

object Board {
  private case class BasicBoard[B <: BoardStructure](boardMap: Map[B#Tile, B#Pawn], structure: B) extends Board[B] {
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

  def apply[B <: BoardStructure](structure: B): Board[B] =
    BasicBoard(Map.empty, structure)
  def apply[B <: BoardStructure](boardMap: Map[B#Tile, B#Pawn])(structure: B): Board[B] =
    BasicBoard(boardMap, structure)
}
