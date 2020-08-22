package sbags.model.dsl

import sbags.model.core.BoardStructure
import sbags.model.extension._

/**
 * Simple action representation.
 *
 * @param run the action to be performed on the state.
 * @tparam G type of game state.
 */
case class Action[G](run: G => G)

/**
 * Enables syntax to work with Actions.
 *
 * @tparam G type of game state.
 */
trait Actions[G] {

  /** An action with no side effect. */
  val doNothing: Action[G] = Action(g => g)

  /**
   * Symbol to start the creation of an action.
   *
   * @param ev implicit value for [[sbags.model.extension.BoardState]].
   * @tparam B type of the board structure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   * @return a new [[sbags.model.dsl.Actions.BoardActions#BoardActions(sbags.model.extension.BoardState)]].
   */
  def >[B <: BoardStructure](implicit ev: BoardState[B, G]): BoardActions[B] =
    new BoardActions

  /** Implicit conversion from Action[G] to a function from G to G. */
  implicit def actionToFunction(action: Action[G]): G => G = action.run

  /** Implicit conversion from a function from G to G to Action[G]. */
  implicit def functionToAction(f: G => G): Action[G] = Action(f)

  /**
   * Enables syntax work on the board and in particular to:
   *
   * - place pawn;<br>
   * - remove pawn;<br>
   * - clear tile;<br>
   * - move a pawn from a tile to another;<br>
   * - swap two pawns;<br>
   * - replace a pawn.<br>
   *
   * @tparam B type of the BoardStructure, with [[sbags.model.core.BoardStructure]] as an upper bound.
   */
  class BoardActions[B <: BoardStructure](implicit ev: BoardState[B, G]) {
    /**
     * Enable syntax to place a pawn on a tile.
     *
     * <p>
     *  This method supports syntax such as the following:
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
    /** This class represents the place pawn operation, don't create this by yourself. Use instead method place. */
    case class PlaceOp(pawn: Feature[G, B#Pawn]) {
      /**
       * Specify the tile where the pawn should be place.
       *
       * @param tile feature that represents the tile.
       * @return the [[sbags.model.dsl.Action]] that represents the place of the pawn on the tile.
       */
      def on(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard(_.place(pawn(s), tile(s))))
    }

    /**
     * Enable syntax to remove a know pawn from a tile.
     *
     * <p>
     *  This method supports syntax such as the following:
     * </p>
     * {{{
     *   remove knowPawnFeature from tileFeature
     *   ^
     * }}}
     *
     * @param pawn the know pawn.
     * @return a [[sbags.model.dsl.Actions.BoardActions.RemoveOp]].
     */
    def remove(pawn: Feature[G, B#Pawn]): RemoveOp = RemoveOp(pawn)
    /** This class represents the remove pawn operation, don't create this by yourself. Use instead method remove. */
    case class RemoveOp(pawn: Feature[G, B#Pawn]) {
      /**
       * Specify from what tile a pawn should be removed.
       *
       * @param tile the feature that represent the tile.
       * @return the [[sbags.model.dsl.Action]] that represent the remove of the pawn from the tile.
       *         This action con throw an [[java.lang.IllegalStateException]].
       */
      def from(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard { board =>
        val actualTile = tile(s)
        if (!board(actualTile).contains(pawn(s)))
          throw new IllegalStateException("Removing pawn from an empty tile")
        board.clear(actualTile)
      })
    }

    /**
     * Enable syntax to clear a tile.
     *
     * @param tile the feature that represents the tile.
     * @return the [[sbags.model.dsl.Action]] that clear the tile.
     */
    def clear(tile: Feature[G, B#Tile]): Action[G] =
      Action(s => s.changeBoard(_.clear(tile(s))))

    /**
     * Enable syntax to move a pawn from a tile to another.
     *
     * <p>
     *  This method supports syntax such as the following:
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
    /** This class represents the move of a pawn from a tile to another, don't create this by yourself. Use instead method moveFrom. */
    case class MoveFromOp(from: Feature[G, B#Tile]) {
      /**
       * Specify the arrival tile.
       *
       * @param tile feature that represents the arrival tile.
       * @return an [[sbags.model.dsl.Action]] that represents the move of a pawn from a tile to another tile.
       *         The action can throw an [[java.lang.IllegalStateException]].
       */
      def to(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard { board =>
        val actualFrom = from(s)
        val movingPawn = board(actualFrom) getOrElse (throw new IllegalStateException("Moving a non present pawn"))
        board.clear(actualFrom).place(movingPawn, tile(s))
      })
    }

    /**
     * Enable syntax to swap the content of two tiles.
     *
     * <p>
     *  This method supports syntax such as the following:
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
    /** This class represents the swap of two tile, don't create this by yourself. Use instead method swap. */
    case class SwapFromOp(from: Feature[G, B#Tile]) {
      /**
       * Specify the other tile for the swap operation.
       *
       * @param tile a feature that represent the other tile.
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
     * Enable syntax to replace a pawn in a tile.
     *
     * @param tile a feature that represent the tile.
     * @return a [[sbags.model.dsl.Actions.BoardActions.ReplaceOp]].
     */
    def replace(tile: Feature[G, B#Tile]): ReplaceOp = ReplaceOp(tile)
    /** This class represents the replace of a pawn in a tile, don't create this by yourself. Use instead method replace. */
    case class ReplaceOp(tile: Feature[G, B#Tile]) {
      /**
       * Specify the action that transform the old pawn in the new one.
       *
       * @param action an action that convert the present pawn in the new one.
       * @return an [[sbags.model.dsl.Action]] that update the pawn present in tile.
       */
      def using(action: B#Pawn => Feature[G, B#Pawn]): Action[G] = Action(s => s.changeBoard { board =>
        val actualTile = tile(s)
        val pawn = board(actualTile) getOrElse (throw new IllegalStateException("Replacing a non present pawn"))
        board.clear(actualTile).place(action(pawn)(s), actualTile)
      })
    }
  }

  /** Action that change the current turn like specify by TurnState implementation. */
  def changeTurn[T](implicit ts: TurnState[T, G]): Action[G] = Action(g => ts.nextTurn(g))

  import Chainables._
  implicit object ChainableActions extends Chainable[Action[G], G, G] {
    override def chain(t1: Action[G], t2: Action[G]): Action[G] = unit(t1.run andThen t2.run)

    override def unit(f: G => G): Action[G] = Action(f)

    override def neutral: Action[G] = unit(g => g)

    override def transform(t: Action[G])(a: G): G = t.run(a)
  }
}
