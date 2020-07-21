package sbags.core.dsl

import sbags.core.{BoardGameState, BoardStructure}
import sbags.core.BoardGameState._

trait Extractors[G, B <: BoardStructure] {
  def tile(implicit ev: BoardGameState[B, G]): G => Seq[B#Tile] =
    _.boardState.structure.tiles

  def emptyTiles(implicit ev: BoardGameState[B, G]): G => Seq[B#Tile] =
    g => tile(ev)(g).filter(t => g.boardState(t).isEmpty)

  def pawn(implicit ev: BoardGameState[B, G]): G => Seq[B#Pawn] =
    g => for (t <- tile(ev)(g); p <- g.boardState(t)) yield p

  def tileWithPawn(implicit ev: BoardGameState[B, G]): G => Seq[(B#Tile, B#Pawn)] =
    g => for (t <- tile(ev)(g); p <- g.boardState(t)) yield (t, p)

  def tileWithOpPawn(implicit ev: BoardGameState[B, G]): G => Seq[(B#Tile, Option[B#Pawn])] =
    g => for (t <- tile(ev)(g)) yield (t, g.boardState(t))

  def tiles(implicit ev: BoardGameState[B, G]): G => Seq[B#Tile] =
    _.boardState.structure.tiles
}
