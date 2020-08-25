package sbags.model.dsl

import sbags.model.core.{Board, BoardStructure, PlacedPawn, RectangularStructure}
import sbags.model.extension._

/**
 * Represents a way to define extensional properties of the game state in an intensional way, using
 * a function to establish how to extract those properties from a concrete instance of the state.
 *
 * @param extract the function that extracts the feature value from the state.
 * @tparam G the type of the game state.
 * @tparam F the feature value type.
 */
case class Feature[G, +F](extract: G => F) {
  /**
   * Extracts the value of this feature from the given state, converting the feature to its extensional value.
   *
   * @param state the state from which to extract the value.
   * @return the extracted value.
   */
  def apply(state: G): F = extract(state)
}

/**
 * This trait collect a series of useful [[sbags.model.dsl.Feature]].
 *
 * @tparam G type of the game state.
 */
trait Features[G] {
  /** A feature that extracts the state as is. */
  def state: Feature[G, G] =
    Feature(s => s)

  /**
   * A feature that extracts the current board state.
   *
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B the type of BoardStructure.
   * @return a feature that represents the board.
   */
  def board[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Board[B]] =
    state map (_.boardState)

  /**
   * A feature that extracts the board structure.
   *
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B the type of BoardStructure.
   * @return a feature that represents the board structure.
   */
  def boardStructure[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, B] =
    board map (_.structure)

  /**
   * A feature that extracts a collection of all tiles.
   *
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B the type of BoardStructure.
   * @return a feature that represents all the tiles in the board.
   */
  def tiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.tiles)

  /**
   * A feature that extracts a row of tiles from the board.
   *
   * @param r the row number.
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B type of RectangularStructure, with [[sbags.model.core.RectangularStructure]] as an upper bound.
   * @return a feature that represents the row r.
   */
  def row[B <: RectangularStructure](r: Int)(implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.row(r))

  /**
   * A feature that extracts a column from the board.
   *
   * @param c the column index.
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B type of RectangularStructure, with [[sbags.model.core.RectangularStructure]] as an upper bound.
   * @return a feature that represents the column c.
   */
  def col[B <: RectangularStructure](c: Int)(implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.col(c))

  /**
   * A feature that extracts all the empty tiles.
   *
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B the type of BoardStructure.
   * @return a feature that represents all the empty tiles.
   */
  def emptyTiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    board map (b => b.structure.tiles filter (b(_).isEmpty))

  /**
   * A feature that extracts a map that represents the current state of the board.
   *
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B the type of BoardStructure.
   * @return a feature that represents the board map.
   */
  def boardMap[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Map[B#Tile, B#Pawn]] =
    board map (_.boardMap)

  /**
   * A feature that extracts all the occupied tiles.
   *
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B the type of BoardStructure.
   * @return a feature that represent the collection of PlacedPawn.
   */
  def occupiedTiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[PlacedPawn[B#Tile, B#Pawn]]] =
    boardMap map (_.toSeq map (x => PlacedPawn(x._2, x._1)))

  /**
   * A feature that extract all the tiles with the related pawn.
   *
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B the type of BoardStructure.
   * @return a feature that represents a collection with all tiles and an optional value to represent the pawn.
   */
  def tilesWithPawns[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[(B#Tile, Option[B#Pawn])]] =
    board map (b => b.structure.tiles map (t => (t, b(t))))

  /**
   * Gives access to the syntax to access the pawn of a specific tile of the board.
   *
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   *
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   pawn optionallyAt (FeatureOfTheTile)
   *   ^
   * }}}
   * or
   * {{{
   *   pawn at (FeatureOfTheTile)
   *   ^
   * }}}
   */
  def pawn[B <: BoardStructure](implicit ev: BoardState[B, G]): PawnSelector[B] = PawnSelector()

  /** This class represents the pawn selector operation. Don't create this by yourself. Use the pawn method instead. */
  case class PawnSelector[B <: BoardStructure](implicit ev: BoardState[B, G]) {
    /**
     * Specifies the target tile and returns a feature representing the content of that tile.
     * The extraction returns an Option that contains the pawn sitting on that tile, if present.
     *
     * @param tile the feature that represents the tile.
     * @return a feature with an optional pawn.
     */
    def optionallyAt(tile: Feature[G, B#Tile]): Feature[G, Option[B#Pawn]] =
      state map (s => s.boardState(tile(s)))

    /**
     * Specifies the target tile and returns a feature representing the content of that tile.
     * The extraction throws an [[java.lang.IllegalStateException]] if no pawn is present.
     *
     * @param tile the feature that represents the tile.
     * @return a feature with a pawn.
     */
    def at(tile: Feature[G, B#Tile]): Feature[G, B#Pawn] =
      optionallyAt(tile) map (_ getOrElse (throw new IllegalStateException("")))
  }

  /**
   * A feature that extracts the current turn.
   *
   * @param ev the [[sbags.model.extension.TurnState]] instance used to access the current turn.
   * @tparam T the type of turn.
   * @return a feature that represents the current turn.
   */
  def currentTurn[T](implicit ev: TurnState[T, G]): Feature[G, T] =
    state map (_.turn)

  /** Implicit conversion from any value to a Feature that always returns that value. */
  implicit def valueToFeature[T](t: T): Feature[G, T] = Feature(_ => t)

  /** Implicit conversion from a function to a feature. */
  implicit def functionToFeature[T](f: G => T): Feature[G, T] = Feature(f)

  /** Implicit conversion from any value to an instance of [[sbags.model.dsl.Features.FeatureOps]]. */
  implicit def valueToFeatureOps[T](t: T): FeatureOps[T] = FeatureOps(t)

  /**
   * Contains useful methods added on top of the [[sbags.model.dsl.Feature]] type. These methods provide a syntax
   * that is closer to natural language to deal with feature manipulation.
   *
   * @param feature the feature to be extended.
   * @tparam F the type of the feature value.
   */
  implicit class FeatureOps[F](feature: Feature[G, F]) {
    /**
     * Maps a feature to another feature using a mapping function.
     *
     * @param f the function to map the value of the feature.
     * @tparam P the new type of the feature.
     * @return a mapped feature.
     */
    def map[P](f: F => P): Feature[G, P] = Feature(g => f(feature(g)))

    /**
     * Map a feature to another feature, using also the state in the map function.
     *
     * @param f the function to map the value of the feature.
     * @tparam P the new type of the feature.
     * @return a mapped feature.
     */
    def map[P](f: (G, F) => P): Feature[G, P] = Feature(g => f(g, feature(g)))

    /**
     * Check if a feature satisfies a condition, using also the state as part of the condition.
     *
     * @param predicate the condition on the state and the feature value.
     * @return a boolean feature which returns the result of the predicate.
     */
    def is(predicate: G => F => Boolean): Feature[G, Boolean] = map(predicate(_)(_))

    /** The inverse of is. See [[sbags.model.dsl.Features.FeatureOps#is(scala.Function1)]] for more details. */
    def isNot(predicate: G => F => Boolean): Feature[G, Boolean] = map(!predicate(_)(_))

    /**
     * Checks if a feature contains a particular value, returning a new feature to describe the equality.
     *
     * @param value the value to be checked.
     * @return a new feature describing the equality.
     */
    def equals(value: F): Feature[G, Boolean] = map(_ == value)
  }

  /**
   * A keyword to be used in conjunction with the is method of features. When used with a feature representing
   * a tile, determines if that tile is empty. This syntax is generally used inside a when condition:
   * {{{
   *   when (tileFeature is empty) {...}
   *                        ^
   * }}}
   *
   * @param ev the [[sbags.model.extension.BoardState]] instance used to access the board state.
   * @tparam B the type of BoardStructure.
   * @tparam T the type of the Tile.
   * @return a predicate that uses a tile and the game state to determine if the tile is empty.
   */
  def empty[B <: BoardStructure, T <: B#Tile](implicit ev: BoardState[B, G]): G => T => Boolean =
    g => t => g.boardState(t).isEmpty
}
