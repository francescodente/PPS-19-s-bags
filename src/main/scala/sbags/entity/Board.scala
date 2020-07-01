package sbags.entity

trait Board[T, P] {
  def apply(tile: T): Option[P]
  def setPawn(tile: T, pawn: P): Board[T, P]
  def removePawn(tile: T): Board[T, P]
}
