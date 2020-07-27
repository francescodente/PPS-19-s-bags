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
