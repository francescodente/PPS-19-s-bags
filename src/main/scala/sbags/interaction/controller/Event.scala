package sbags.interaction.controller

case class TileSelected(x: Int, y: Int) extends Event
case class PawnSelected(pawnName: String) extends Event
case object Done extends Event
