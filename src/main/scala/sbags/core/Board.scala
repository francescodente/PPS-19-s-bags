package sbags.core

/**
 * Represents the definition of a generic Board, providing the basic functionality to work with it.
 * In particular, a Board is a set of logically structured Tiles.
 */
trait Board {
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
   * Returns a [[scala.Option]] describing the pawn sitting at the given tile.
   * If the tile is empty [[scala.None]] is returned, otherwise the pawn is wrapped in a [[scala.Option]]
   * and returned.
   *
   * @param tile the tile to be queried.
   * @return Some(pawn) if the tile is occupied, None otherwise.
   */
  def apply(tile: Tile): Option[Pawn]

  /**
   * Sets the given pawn on the given tile, throwing an [[IllegalStateException]]
   * if the tile is not empty.
   *
   * @param pawn the pawn to be placed on the tile.
   * @param tile the tile where the pawn should be placed.
   * @return a [[sbags.core.Board]] with pawn into tile if the tile was empty.
   * @throws IllegalStateException if tile is not empty.
   */
  def setPawn(pawn: Pawn, tile: Tile): this.type

  /**
   * Removes the pawn sitting on the given tile, throwing an [[IllegalStateException]] if the tile is empty.
   *
   * @param tile the tile to empty out.
   * @return a [[sbags.core.Board]] with the tile empty.
   * @throws IllegalStateException if tile is empty.
   */
  def removePawn(tile: Tile): this.type

  /**
   * An alias for setPawn that takes the arguments
   * as a tuple, allowing the following syntax to be used:
   * {{{board << (pawn -> tile)}}}
   */
  def <<(placedPawn: (Pawn, Tile)): this.type = setPawn(placedPawn._1, placedPawn._2)

  /**
   * An alias for removePawn.
   */
  def <#(tile: Tile): this.type = removePawn(tile)

  /**
   * Returns the board representation as a [[scala.collection.Map]].
   * @return the board representation as a [[scala.collection.Map]].
   */
  def boardMap: Map[Tile, Pawn]

  /**
   * Returns the sequence containing all the valid tile for this board.
   * @return the sequence containing all the valid tile for this board.
   */
  def tiles: Seq[Tile]
}

/**
 * Represents an abstract basic implementation for the [[sbags.core.Board]] trait.
 *
 * This allow users to only have to declare the Tile and Pawn types.
 */
abstract class BasicBoard extends Board {
  private var pawnPositions: Map[Tile, Pawn] = Map()

  override def apply(tile: Tile): Option[Pawn] = {
    if (pawnPositions contains tile) Some(pawnPositions(tile))
    else None
  }

  override def setPawn(pawn: Pawn, tile: Tile): this.type = {
    this(tile) match {
      case Some(_) => throw new IllegalStateException
      case None => pawnPositions = pawnPositions + (tile -> pawn)
    }
    this
  }

  override def removePawn(tile: Tile): this.type = {
    this(tile) match {
      case Some(_) => pawnPositions = pawnPositions - tile
      case None => throw new IllegalStateException
    }
    this
  }

  override def boardMap: Map[Tile, Pawn] = pawnPositions
}

/**
 * Create a rectangular board that use two Int to define the position of a tile.
 * In particular an (x, y) position is valid if 0 &lt= x &lt width and 0 &lt= y &lt height
 */
trait RectangularBoard extends Board {
  type Tile = Coordinate
  val width: Int
  val height: Int

  private def isAValidTile(tile: Tile): Boolean = tile.x >= 0 && tile.x < width && tile.y >= 0 && tile.y < height

  private def assertTileIsValid(tile: Tile): Unit = {
    if (!isAValidTile(tile))
      throw new IllegalArgumentException
  }

  abstract override def setPawn(pawn: Pawn, tile: Tile): this.type = {
    assertTileIsValid(tile)
    super.setPawn(pawn, tile)
  }

  abstract override def removePawn(tile: Coordinate): this.type = {
    assertTileIsValid(tile)
    super.removePawn(tile)
  }

  override def tiles: Seq[Coordinate] = for (x <- 0 until width; y <- 0 until height) yield Coordinate(x, y)
}

/**
 * Represents a classic implementation for the [[sbags.core.Board]] trait.
 */
class BasicRectangularBoard(val width: Int, val height: Int) extends BasicBoard with RectangularBoard

trait SquareBoard extends RectangularBoard {
  val size: Int

  override val width: Int = size
  override val height: Int = size
}

class BasicSquareBoard(val size: Int) extends BasicRectangularBoard(size, size) with SquareBoard