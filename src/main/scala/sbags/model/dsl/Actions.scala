package sbags.model.dsl

import sbags.model.core.BoardStructure
import sbags.model.extension._

/**
 * Represents an action that can be executed on a state of type G, that is a transformation from one
 * state to another.
 *
 * @param run the function that transforms the state.
 * @tparam G type of game state.
 */
case class Action[G](run: G => G)

/**
 * Enables syntax to work with [[sbags.model.dsl.Action]]s and to use the predefined set of commonly used
 * actions.
 *
 * @tparam G type of game state.
 */
trait Actions[G] {

  /** An [[sbags.model.dsl.Action]] that has no effect. */
  val doNothing: Action[G] = Action(g => g)

  /**
   * Symbol to start the definition of an [[sbags.model.dsl.Action]] that modifies the state of the board.
   *
   * @param ev implicit value for [[sbags.model.extension.BoardState]].
   * @tparam B type of the board structure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @return an object that enables syntax to work with the board.
   */
  def >[B <: BoardStructure](implicit ev: BoardState[B, G]): BoardActions[B] =
    new BoardActions

  /** Implicit conversion from Action[G] to a function from G to G. */
  implicit def actionToFunction(action: Action[G]): G => G = action.run

  /** Implicit conversion from a function from G to G to Action[G]. */
  implicit def functionToAction(f: G => G): Action[G] = Action(f)

  /**
   * Enables syntax work on the board and in particular to:
   * <ul>
   * <li>place a pawn;</li>
   * <li>remove a pawn;</li>
   * <li>clear a tile;</li>
   * <li>move a pawn from a tile to another;</li>
   * <li>swap two pawns;</li>
   * <li>replace a pawn.</li>
   * </ul>
   *
   * @tparam B type of the BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   */
  class BoardActions[B <: BoardStructure](implicit ev: BoardState[B, G]) {
    /**
     * Enables syntax to place a pawn on a tile.
     *
     * <p>
     * This method supports syntax such as the following:
     * </p>
     * {{{
     *   place pawnFeature on tileFeature
     *   ^
     * }}}
     *
     * @param pawn feature to extract the Pawn.
     * @return a [[sbags.model.dsl.Actions.BoardActions.PlaceOp]].
     */
    def place(pawn: Feature[G, B#Pawn]): PlaceOp = PlaceOp(pawn)

    /** This class represents the place pawn operation. Don't create this by yourself. Use the place method instead. */
    case class PlaceOp(pawn: Feature[G, B#Pawn]) {
      /**
       * Specifies the tile where the pawn should be place.
       *
       * @param tile feature that represents the tile.
       * @return an [[sbags.model.dsl.Action]] that places the pawn on the tile.
       */
      def on(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard(_.place(pawn(s), tile(s))))
    }

    /**
     * Enables syntax to remove a specific pawn from a tile.
     *
     * <p>
     * This method supports syntax such as the following:
     * </p>
     * {{{
     *   remove knownPawnFeature from tileFeature
     *   ^
     * }}}
     *
     * @param pawn the know pawn.
     * @return a [[sbags.model.dsl.Actions.BoardActions.RemoveOp]].
     */
    def remove(pawn: Feature[G, B#Pawn]): RemoveOp = RemoveOp(pawn)

    /** Represents the remove pawn operation. Don't create this by yourself. Use the remove method instead. */
    case class RemoveOp(pawn: Feature[G, B#Pawn]) {
      /**
       * Specifies from what tile the pawn should be removed.
       *
       * @param tile the feature that represents the tile.
       * @return an [[sbags.model.dsl.Action]] that removes the pawn from the tile.
       *         This action con throw an [[java.lang.IllegalStateException]].
       */
      def from(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard { board =>
        val actualTile = tile(s)
        if (!board(actualTile).contains(pawn(s)))
          throw new IllegalStateException("Removing an incorrect pawn or clearing an empty tile")
        board.clear(actualTile)
      })
    }

    /**
     * Enables syntax to clear a tile.
     *
     * @param tile the feature that represents the tile.
     * @return an [[sbags.model.dsl.Action]] that clears the tile.
     */
    def clear(tile: Feature[G, B#Tile]): Action[G] =
      Action(s => s.changeBoard(_.clear(tile(s))))

    /**
     * Enables syntax to move a pawn from a tile to another.
     *
     * <p>
     * This method supports syntax such as the following:
     * </p>
     * {{{
     *   moveFrom tileFromFeature to tileToFeature
     *   ^
     * }}}
     *
     * @param tile a feature that represents the starting tile.
     * @return a [[sbags.model.dsl.Actions.BoardActions.MoveFromOp]].
     */
    def moveFrom(tile: Feature[G, B#Tile]): MoveFromOp = MoveFromOp(tile)

    /** This class represents the move of a pawn from a tile to another. Don't create this by yourself. Use the moveFrom method instead. */
    case class MoveFromOp(from: Feature[G, B#Tile]) {
      /**
       * Specifies the target tile.
       *
       * @param tile feature that represents the target tile.
       * @return an [[sbags.model.dsl.Action]] that moves the pawn from the source tile to the target tile.
       *         The action can throw an [[java.lang.IllegalStateException]].
       */
      def to(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard { board =>
        val actualFrom = from(s)
        val movingPawn = board(actualFrom) getOrElse (throw new IllegalStateException("Moving from an empty tile"))
        board.clear(actualFrom).place(movingPawn, tile(s))
      })
    }

    /**
     * Enables syntax to swap the content of two tiles.
     *
     * <p>
     * This method supports syntax such as the following:
     * </p>
     * {{{
     *   swap tile1Feature and tile2Feature
     *   ^
     * }}}
     *
     * @param tile a feature that represents a tile.
     * @return a [[sbags.model.dsl.Actions.BoardActions.SwapFromOp]].
     */
    def swap(tile: Feature[G, B#Tile]): SwapFromOp = SwapFromOp(tile)

    /** This class represents the swap of two tile. Don't create this by yourself. Use the swap method instead. */
    case class SwapFromOp(from: Feature[G, B#Tile]) {
      /**
       * Specifies the other tile for the swap operation.
       *
       * @param tile a feature that represents the other tile.
       * @return an [[sbags.model.dsl.Action]] that represents the swap of two tiles.
       */
      def and(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard { b =>
        val fromTile = from(s)
        val toTile = tile(s)
        var board = b
        val pawnInFrom = b(fromTile)
        val pawnInTo = b(toTile)

        if (pawnInFrom.isDefined) board = board.clear(fromTile)
        if (pawnInTo.isDefined) board = board.clear(toTile)

        for (pawn <- pawnInFrom) board = board.place(pawn, toTile)
        for (pawn <- pawnInTo) board = board.place(pawn, fromTile)
        board
      })
    }

    /**
     * Enables syntax to replace a pawn in a tile.
     *
     * @param tile a feature that represent the tile.
     * @return a [[sbags.model.dsl.Actions.BoardActions.ReplaceOp]].
     */
    def replace(tile: Feature[G, B#Tile]): ReplaceOp = ReplaceOp(tile)

    /** Represents the replacement of a pawn in a tile. Don't create this by yourself. Use the replace method instead. */
    case class ReplaceOp(tile: Feature[G, B#Tile]) {
      /**
       * Specifies the action that transforms the old pawn into the new one.
       *
       * @param action an action that converts the current pawn into the new one.
       * @return an [[sbags.model.dsl.Action]] that replaces the pawn in the target tile using the given function.
       */
      def using(action: B#Pawn => Feature[G, B#Pawn]): Action[G] = Action(s => s.changeBoard { board =>
        val actualTile = tile(s)
        val pawn = board(actualTile) getOrElse (throw new IllegalStateException("Replacing a pawn on an empty tile"))
        board.clear(actualTile).place(action(pawn)(s), actualTile)
      })
    }
  }

  /**
   * Returns an [[sbags.model.dsl.Action]] that advances the current turn according to what's specified by the
   * given [[sbags.model.extension.TurnState]] instance.
   *
   * @param ts the [[sbags.model.extension.TurnState]] instance.
   * @tparam T the turn type.
   * @return a new [[sbags.model.dsl.Action]] that advances the turn.
   */
  def changeTurn[T](implicit ts: TurnState[T, G]): Action[G] = Action(g => ts.nextTurn(g))

  import Chainables._

  /**
   * Defines a [[sbags.model.dsl.Chainables.Chainable]] instance to be used with [[sbags.model.dsl.Action]]s, that
   * implements chaining as the ordered execution of the two chained actions.
   */
  implicit object ChainableActions extends Chainable[Action[G], G, G] {
    override def chain(t1: Action[G], t2: Action[G]): Action[G] = unit(t1.run andThen t2.run)

    override def unit(f: G => G): Action[G] = Action(f)

    override def neutral: Action[G] = unit(g => g)

    override def transform(t: Action[G])(a: G): G = t.run(a)
  }
}
