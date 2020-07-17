package sbags.interaction.controller

/**
 * An event to be emitted by a user interface, representing a user's interaction.
 * Some examples are selecting a tile, selecting a pawn, ecc.
 * A particularly important type of event is [[sbags.interaction.controller.Done]], which terminates a sequence of events and executes a move if a match is found.
 */
trait Event
case class TileSelected(x: Int, y: Int) extends Event
case class PawnSelected(pawnName: String) extends Event
case object Done extends Event
