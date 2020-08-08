package sbags.core

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

  def containsTile[T >: Tile](tile: T): Boolean = tiles.contains(tile)
}

class RectangularStructure(val width: Int, val height: Int) extends BoardStructure {
  type Tile = Coordinate

  override def tiles: Seq[Coordinate] = for (x <- 0 until width; y <- 0 until height) yield Coordinate(x, y)
}

class SquareStructure(val size: Int) extends RectangularStructure(size, size)
