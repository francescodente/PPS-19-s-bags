package sbags.core.dsl

import sbags.core.{Board, BoardStructure, PlacedPawn, RectangularStructure}
import sbags.core.extension._

case class Feature[G, +F](extractor: G => F) {
  def has(predicate: F => Boolean): G => Boolean = g => predicate(extractor(g))
  def is(predicate: G => F => Boolean): G => Boolean = g => predicate(g)(extractor(g))
  def isNot(predicate: G => F => Boolean): G => Boolean = g => !predicate(g)(extractor(g))
  def equals[A](value: A): G => Boolean = g => extractor(g) == value
  def apply(predicate: F => Boolean): G => Boolean = g => predicate(extractor(g))
  def apply(state: G): F = extractor(state)
  def map[P](f: F => P): Feature[G, P] = Feature(g => f(extractor(g)))
  def map[P](f: (G, F) => P): Feature[G, P] = Feature(g => f(g, extractor(g)))
}

trait Features[G] {
  def state: Feature[G, G] =
    Feature(s => s)

  def board[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Board[B]] =
    state map (_.boardState)

  def boardStructure[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, B] =
    board map (_.structure)

  def tiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.tiles)

  def row[B <: RectangularStructure](r: Int)(implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.row(r))

  def col[B <: RectangularStructure](c: Int)(implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.col(c))

  def emptyTiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    board map (b => b.structure.tiles filter (b(_).isEmpty))

  def boardMap[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Map[B#Tile, B#Pawn]] =
    board map (_.boardMap)

  def occupiedTiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[PlacedPawn[B#Tile, B#Pawn]]] =
    boardMap map (_.toSeq map (x => PlacedPawn(x._2, x._1)))

  def tilesWithPawns[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[(B#Tile, Option[B#Pawn])]] =
    board map (b => b.structure.tiles map (t => (t, b(t))))

  def pawn[B <: BoardStructure](implicit ev: BoardState[B, G]): PawnSelector[B] = PawnSelector()
  case class PawnSelector[B <: BoardStructure](implicit ev: BoardState[B, G]) {
    def optionallyAt(tile: Feature[G, B#Tile]): Feature[G, Option[B#Pawn]] =
      state map (s => s.boardState(tile(s)))

    def at(tile: Feature[G, B#Tile]): Feature[G, B#Pawn] =
      optionallyAt(tile) map (_ getOrElse (throw new IllegalStateException))
  }

  def currentTurn[T](implicit ev: TurnState[T, G]): Feature[G, T] =
    state map (_.turn)

  implicit def valueToFeature[T](t: T): Feature[G, T] = Feature(_ => t)

  implicit def functionToFeature[T](f: G => T): Feature[G, T] = Feature(f)

  def empty[B <: BoardStructure, T <: B#Tile](implicit ev: BoardState[B, G]): G => T => Boolean =
    g => t => g.boardState(t).isEmpty
}
