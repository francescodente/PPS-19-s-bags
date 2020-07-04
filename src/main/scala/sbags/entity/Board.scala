package sbags.entity

/**
 * Represent the definition of a generic Board, providing the basic functionality to work with it.
 *
 * In particular a Board is a set of logically structured Tiles.
 */
trait Board {
  /**
   * Define the type of the tile of the [[sbags.entity.Board]].
   *
   * For example:
   *  If you want to declare a 2D board you can assigning (Int, Int) type to Tile.
   */
  type Tile

  /**
   * Define the type of the pawn of the [[sbags.entity.Board]].
   */
  type Pawn

  /**
   * @param tile a tile
   * @return the pawn associate with the tile. None is returned if tile is not present or empty.
   */
  def apply(tile: Tile): Option[Pawn]

  /**
   * @param pawn the pawn to be set in the board.
   * @param tile the tile where the pawn should be placed.
   * @return a [[sbags.entity.Board]] with pawn into tile if the tile was empty.
   */
  def setPawn(pawn: Pawn, tile: Tile): this.type

  /**
   * @param tile the tile to empty.
   * @return a [[sbags.entity.Board]] with the tile empty.
   */
  def removePawn(tile: Tile): this.type

  /**
   * See [[sbags.entity.Board#setPawn(java.lang.Object, java.lang.Object)]].
   */
  def <<(placedPawn : (Pawn, Tile)): this.type = setPawn(placedPawn._1, placedPawn._2)

  /**
   * See [[sbags.entity.Board#removePawn(java.lang.Object)]].
   */
  def <#(tile: Tile): this.type = removePawn(tile)
}

/**
 * Represent an abstract basic implementation for the [[sbags.entity.Board]] trait.
 *
 * This allow users to have only to declare Tile and Pawn type.
 */
abstract class BasicBoard() extends Board {
  private var boardMap: Map[Tile, Pawn] = Map()

  override def apply(tile: Tile): Option[Pawn] = {
    if (boardMap contains tile) Some(boardMap(tile))
    else None
  }

  override def setPawn(pawn: Pawn, tile: Tile): BasicBoard.this.type = {
    this (tile) match {
      case Some(_) => throw new IllegalStateException
      case None => boardMap += (tile -> pawn)
    }
    this
  }

  override def removePawn(tile: Tile): BasicBoard.this.type = {
    this (tile) match {
      case Some(_) => boardMap -= tile
      case None => throw new IllegalStateException
    }
    this
  }
}
