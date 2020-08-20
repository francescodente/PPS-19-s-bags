package sbags.model.dsl

import sbags.model.core.{Board, BoardStructure, PlacedPawn, RectangularStructure}
import sbags.model.extension._

/**
 * TODO
 *
 * @param extractor TODO
 * @tparam G type of the game state.
 * @tparam F the feature type.
 */
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

/**
 *
 * @tparam G type of the game state.
 */
trait Features[G] {
  /** A feature to work on state. */
  def state: Feature[G, G] =
    Feature(s => s)

  /** A feature to work on the board state. */
  def board[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Board[B]] =
    state map (_.boardState)

  /** A feature to work on the board structure. */
  def boardStructure[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, B] =
    board map (_.structure)

  /** A feature to work on all tiles of the board. */
  def tiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.tiles)

  /** A feature to work on a particular row of a RectangularStructure. */
  def row[B <: RectangularStructure](r: Int)(implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.row(r))

  /** A feature to work on a particular col of a RectangularStructure. */
  def col[B <: RectangularStructure](c: Int)(implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.col(c))

  /** A feature to work on empty tiles. */
  def emptyTiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    board map (b => b.structure.tiles filter (b(_).isEmpty))

  /** A feature to work on a board as a map(Tile, Pawn). */
  def boardMap[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Map[B#Tile, B#Pawn]] =
    board map (_.boardMap)

  /** A feature to work on occupied tiles. */ // TODO end
  def occupiedTiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[PlacedPawn[B#Tile, B#Pawn]]] =
    boardMap map (_.toSeq map (x => PlacedPawn(x._2, x._1)))

  /** A feature to work on */// TODO end
  def tilesWithPawns[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[(B#Tile, Option[B#Pawn])]] =
    board map (b => b.structure.tiles map (t => (t, b(t))))

  /** A feature to work on */// TODO end
  def pawn[B <: BoardStructure](implicit ev: BoardState[B, G]): PawnSelector[B] = PawnSelector()
  case class PawnSelector[B <: BoardStructure](implicit ev: BoardState[B, G]) {
    def optionallyAt(tile: Feature[G, B#Tile]): Feature[G, Option[B#Pawn]] =
      state map (s => s.boardState(tile(s)))

    def at(tile: Feature[G, B#Tile]): Feature[G, B#Pawn] =
      optionallyAt(tile) map (_ getOrElse (throw new IllegalStateException))
  }

  /** A feature to work on */// TODO end
  def currentTurn[T](implicit ev: TurnState[T, G]): Feature[G, T] =
    state map (_.turn)

  /** Implicit conversion from value to Feature of that value. */
  implicit def valueToFeature[T](t: T): Feature[G, T] = Feature(_ => t)

  /** Implicit conversion from a function to a feature. */
  implicit def functionToFeature[T](f: G => T): Feature[G, T] = Feature(f)

  /** A feature to work on */ // TODO end
  def empty[B <: BoardStructure, T <: B#Tile](implicit ev: BoardState[B, G]): G => T => Boolean =
    g => t => g.boardState(t).isEmpty
}
