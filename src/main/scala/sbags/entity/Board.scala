package sbags.entity

trait Board {
  type Tile
  type Pawn

  def apply(tile: Tile): Option[Pawn]

  def setPawn(pawn: Pawn, tile: Tile): this.type
  def removePawn(tile: Tile): this.type

  def <<(placedPawn : (Pawn, Tile)): this.type = setPawn(placedPawn._1, placedPawn._2)
  def <#(tile: Tile): this.type = removePawn(tile)
}

abstract class BasicBoard() extends Board {
  private var boardMap: Map[Tile, Pawn] = Map()

  override def apply(tile: Tile): Option[Pawn] = {
    if (boardMap contains tile) Some(boardMap(tile))
    else None
  }

  override def setPawn(pawn: Pawn, tile: Tile): BasicBoard.this.type = {
    this (tile) match {
      case Some(_) => throw new IllegalStateException
      case None => boardMap += (tile -> pawn)
    }
    this
  }

  override def removePawn(tile: Tile): BasicBoard.this.type = {
    this (tile) match {
      case Some(_) => boardMap -= tile
      case None => throw new IllegalStateException
    }
    this
  }
}
