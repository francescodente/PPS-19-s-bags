package sbags.interaction.view

/**
 * An event to be emitted by a user interface, representing a user's interaction.
 * Some examples are selecting a tile, selecting a pawn, ecc.
 */
trait Event

/**
 * An [[Event]] representing the selection of a Tile.
 * @param x the row.
 * @param y the tile.
 */
case class TileSelected(x: Int, y: Int) extends Event

/**
 * An [[Event]] representing the selection of a Pawn.
 * @param pawnName the identifier of the selected pawn.
 */
case class PawnSelected(pawnName: String) extends Event

/**
 * An [[Event]] representing the selection of a Lane (column or row).
 * @param lane the identifier of the selected lane.
 */
case class LaneSelected(lane: Int) extends Event

/** An [[Event]] representing the exit from the Game. */
object Quit extends Event
