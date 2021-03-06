package sbags.model.core

/**
 * Represents the structure of a generic [[sbags.model.core.Board]]
 * In particular, this is a set of logically structured Tiles.
 */
trait BoardStructure {
  /**
   * Defines the type of the tiles that compose the [[sbags.model.core.Board]].
   * For example, to declare a 2D board the type (Int, Int) can be assigned to this.
   */
  type Tile

  /** Defines the type of the pawn that can be placed on the [[sbags.model.core.Board]]. */
  type Pawn

  /** Returns the sequence containing all the valid tile for this board. */
  def tiles: Seq[Tile]

  /**
   * Checks if a tile is present in the structure.
   *
   * @param tile the tile to be checked.
   * @tparam T type of the tile with upper bound [[sbags.model.core.BoardStructure#Tile]].
   * @return true if the tile is present, false otherwise.
   */
  def containsTile[T >: Tile](tile: T): Boolean = tiles.contains(tile)
}

/**
 * Represents a Classic implementation of a [[sbags.model.core.BoardStructure]]:
 * a rectangular structure with a width and height.
 *
 * @param width the number of columns.
 * @param height the number of rows.
 */
class RectangularStructure(val width: Int, val height: Int) extends BoardStructure {
  type Tile = Coordinate

  override def tiles: Seq[Coordinate] = for (x <- 0 until width; y <- 0 until height) yield Coordinate(x, y)
}

/**
 * Represents a particular [[sbags.model.core.RectangularStructure]]: a square one.
 *
 * @param size the dimension of the side.
 */
class SquareStructure(val size: Int) extends RectangularStructure(size, size)
