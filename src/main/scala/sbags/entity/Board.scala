package sbags.entity

trait Board {
  type Tile
  type Pawn

  type BoardType = Board {
    type Tile = Board.this.Tile
    type Pawn = Board.this.Pawn
  }

  def apply(tile: Tile): Option[Pawn]
  def setPawn(tile: Tile, pawn: Pawn): BoardType
  def removePawn(tile: Tile): BoardType
}
