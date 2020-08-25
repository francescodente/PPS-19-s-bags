package sbags.model.dsl

import sbags.model.core.{Board, BoardStructure, PlacedPawn, RectangularStructure}
import sbags.model.extension._

/** TODO check
 * This class represent extensional game state in an intensional scenario.
 *
 * @param extractor the function that extract the feature from the state.
 * @tparam G type of the game state.
 * @tparam F the feature type.
 */
case class Feature[G, +F](extractor: G => F) {
  /**
   * Check if a feature satisfies a condition.
   *
   * @param predicate the condition on the element extract from the state.
   * @return a function that apply the predicate of the feature to a state.
   */
  def has(predicate: F => Boolean): G => Boolean = g => predicate(extractor(g))

  /**
   * Check if a feature satisfies a condition.
   *
   * @param predicate the condition on the state and the element.
   * @return a function that apply the predicate of the feature to a state.
   */
  def is(predicate: G => F => Boolean): G => Boolean = g => predicate(g)(extractor(g))

  /** The negation of is. See [[sbags.model.dsl.Feature#is]] for more details. */
  def isNot(predicate: G => F => Boolean): G => Boolean = g => !predicate(g)(extractor(g))

  /**
   * Check if a feature represent a particular value.
   *
   * @param value the value to be checked.
   * @tparam A the type of the value.
   * @return a function that apply the check to a state.
   */
  def equals[A](value: A): G => Boolean = g => extractor(g) == value

  /** Same as [[sbags.model.dsl.Feature#has]]. */
  def apply(predicate: F => Boolean): G => Boolean = has(predicate)

  /**
   * Convert the feature to its extensional value.
   *
   * @param state the state from which extract the value.
   * @return the extracted value.
   */
  def apply(state: G): F = extractor(state)

  /**
   * Map a feature to another feature.
   *
   * @param f the function to map the value of the feature.
   * @tparam P the new type of the feature.
   * @return a mapped feature.
   */
  def map[P](f: F => P): Feature[G, P] = Feature(g => f(extractor(g)))

  /**
   * Map a feature to another feature.
   * Using also the state in the map function.
   *
   * @param f the function to map the value of the feature.
   * @tparam P the new type of the feature.
   * @return a mapped feature.
   */
  def map[P](f: (G, F) => P): Feature[G, P] = Feature(g => f(g, extractor(g)))
}

/**
 * This trait collect a series of useful [[sbags.model.dsl.Feature]].
 *
 * @tparam G type of the game state.
 */
trait Features[G] {
  /** A feature that extract the state. */
  def state: Feature[G, G] =
    Feature(s => s)

  /**
   * A feature that extract the board state.
   *
   * @param ev an implicit structure to work with the Board of a state. TODO <--- check this and update.
   * @tparam B type of BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @return a feature that represents the board.
   */
  def board[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Board[B]] =
    state map (_.boardState)

  /**
   * A feature that extract the board structure.
   *
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   * @tparam B type of BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @return a feature that represents the board structure.
   */
  def boardStructure[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, B] =
    board map (_.structure)

  /**
   * A feature that extract a sequence of tiles.
   *
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   * @tparam B type of BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @return a feature that represents all tiles present in the board.
   */
  def tiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.tiles)
  /**
   * A feature that extract a row from the board.
   *
   * @param r row number.
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   * @tparam B type of RectangularStructure, with [[sbags.model.core.RectangularStructure]] as an upper bound.
   * @return a feature that represents the row r.
   */
  def row[B <: RectangularStructure](r: Int)(implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.row(r))

  /**
   * A feature that extract a column from the board.
   *
   * @param c column number.
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   * @tparam B type of RectangularStructure, with [[sbags.model.core.RectangularStructure]] as an upper bound.
   * @return a feature that represents the column c.
   */
  def col[B <: RectangularStructure](c: Int)(implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    boardStructure map (_.col(c))

  /**
   * A feature that extract all the empty tiles.
   *
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   * @tparam B type of BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @return a feature that represents all the empty tiles.
   */
  def emptyTiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[B#Tile]] =
    board map (b => b.structure.tiles filter (b(_).isEmpty))

  /**
   * A feature that extract a map that represents the current board.
   *
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   * @tparam B type of BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @return a feature that represents the map of Tile and Pawn extract from the board.
   */
  def boardMap[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Map[B#Tile, B#Pawn]] =
    board map (_.boardMap)

  /**
   * A feature that extract all the occupied tiles.
   * A tile is occupied if a pawn is present on it.
   *
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   * @tparam B type of BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @return a feature that represent the sequence of PlacedPawn.
   */
  def occupiedTiles[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[PlacedPawn[B#Tile, B#Pawn]]] =
    boardMap map (_.toSeq map (x => PlacedPawn(x._2, x._1)))

  /**
   * A feature that extract all the tiles with the relative pawn.
   *
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   * @tparam B type of BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @return a feature that represents a sequence with all tiles and an optional value to represent the pawn.
   */
  def tilesWithPawns[B <: BoardStructure](implicit ev: BoardState[B, G]): Feature[G, Seq[(B#Tile, Option[B#Pawn])]] =
    board map (b => b.structure.tiles map (t => (t, b(t))))

  /**
   * This method give access to the syntax to work with specific pawn.
   *
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   *
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   pawn optionallyAt (FeatureOfTheTile)
   *   ^
   * }}}
   * <p>
   *  This return a Feature[G, Optional[B#Pawn] ]
   * </p>
   * <p>
   *   or
   * </p>
   * {{{
   *   pawn at (FeatureOfTheTile)
   *   ^
   * }}}
   * <p>
   *  This return a Feature[G, B#Pawn], but throw an IllegalStateException if the pawn is not present.
   * </p>
   */
  def pawn[B <: BoardStructure](implicit ev: BoardState[B, G]): PawnSelector[B] = PawnSelector()

  /** This class represents the pawn selector operation, don't create this by yourself. Use instead method pawn of the DSL. */
  case class PawnSelector[B <: BoardStructure](implicit ev: BoardState[B, G]) {
    /**
     * Specify the position of the pawn and take the result as optional.
     *
     * @param tile the feature that represents the tile.
     * @return a feature with an optional pawn.
     */
    def optionallyAt(tile: Feature[G, B#Tile]): Feature[G, Option[B#Pawn]] =
      state map (s => s.boardState(tile(s)))

    /**
     * Specify the position of the pawn.
     *
     * @param tile the feature that represents the tile.
     * @return a feature with a pawn. The extraction can throw an `IllegalStateException` if no pawn is present.
     */
    @throws[IllegalStateException]
    def at(tile: Feature[G, B#Tile]): Feature[G, B#Pawn] =
      optionallyAt(tile) map (_ getOrElse (throw new IllegalStateException))
  }

  /**
   * A feature that extract the current turn.
   *
   * @param ev an implicit structure to work with turn.
   * @tparam T type of the turn.
   * @return a feature that represent the current turn.
   */
  def currentTurn[T](implicit ev: TurnState[T, G]): Feature[G, T] =
    state map (_.turn)

  /** Implicit conversion from value to Feature of that value. */
  implicit def valueToFeature[T](t: T): Feature[G, T] = Feature(_ => t)

  /** Implicit conversion from a function to a feature. */
  implicit def functionToFeature[T](f: G => T): Feature[G, T] = Feature(f)

  /**
   * A feature that extract TODO.
   *
   * @param ev an implicit structure to work with the Board state. TODO <--- check this and update.
   * @tparam B type of BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @tparam T type of a Tile, with [[sbags.model.core.BoardStructure#Tile]] as an upper bound.
   * @return a feature that represents TODO.
   */
  def empty[B <: BoardStructure, T <: B#Tile](implicit ev: BoardState[B, G]): G => T => Boolean =
    g => t => g.boardState(t).isEmpty
}
