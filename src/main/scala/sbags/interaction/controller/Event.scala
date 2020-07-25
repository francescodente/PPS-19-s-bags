package sbags.interaction.controller

/**
 * An event to be emitted by a user interface, representing a user's interaction.
 * Some examples are selecting a tile, selecting a pawn, ecc.
 * A particularly important type of event is [[sbags.interaction.controller.Done]], which terminates a sequence of events and executes a move if a match is found.
 */
trait Event

/**
 * An [[sbags.interaction.controller.Event]] representing the event of selecting a Tile.
 * @param x the row.
 * @param y the tile.
 */
case class TileSelected(x: Int, y: Int) extends Event

/**
 * An [[sbags.interaction.controller.Event]] representing the event of selecting a Pawn.
 * @param pawnName the identifier of the selected pawn.
 */
case class PawnSelected(pawnName: String) extends Event

/**
 * An [[sbags.interaction.controller.Event]] representing the event of selecting a Lane (column or row).
 * @param lane the identifier of the selected lane.
 */
case class LaneSelected(lane: Int) extends Event

/**
 * A special [[sbags.interaction.controller.Event]] representing the end of the sequence of events.
 * It signals that a move should be created combining the previous events composing it, and that the next events will be part of a different move.
 */
case object Done extends Event


