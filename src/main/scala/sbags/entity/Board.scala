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
  var boardMap: Map[Tile, Pawn] = Map()

  private def checkTileAndAct[B](tile: Tile)(trueBranch: => B, falseBranch: => B): B = {
    if (boardMap contains tile) trueBranch
    else falseBranch
  }

  override def apply(tile: Tile): Option[Pawn] = checkTileAndAct(tile)(Some(boardMap(tile)),None)

  override def setPawn(pawn: Pawn, tile: Tile): BasicBoard.this.type = {
    checkTileAndAct(tile)(throw new IllegalStateException, boardMap += (tile -> pawn))
    this
  }

  override def removePawn(tile: Tile): BasicBoard.this.type = {
    checkTileAndAct(tile)(boardMap -= tile, throw new IllegalArgumentException)
    this
  }
}
