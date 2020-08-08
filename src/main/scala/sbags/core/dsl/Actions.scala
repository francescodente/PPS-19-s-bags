package sbags.core.dsl

import sbags.core.extension._
import sbags.core.BoardStructure

case class Action[G](run: G => G)

trait Actions[G] {
  def doNothing: Action[G] = Action(g => g)

  def >[B <: BoardStructure](implicit ev: BoardState[B, G]): BoardActions[B] =
    new BoardActions

  implicit def actionToFunction(action: Action[G]): G => G = action.run
  implicit def functionToAction(f: G => G): Action[G] = Action(f)

  class BoardActions[B <: BoardStructure](implicit ev: BoardState[B, G]) {
    def place(pawn: Feature[G, B#Pawn]): PlaceOp = PlaceOp(pawn)
    case class PlaceOp(pawn: Feature[G, B#Pawn]) {
      def on(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard(_.place(pawn(s), tile(s))))
    }

    def remove(pawn: Feature[G, B#Pawn]): RemoveOp = RemoveOp(pawn)
    case class RemoveOp(pawn: Feature[G, B#Pawn]) {
      def from(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard { board =>
        val actualTile = tile(s)
        if (!board(actualTile).contains(pawn(s)))
          throw new IllegalStateException("Removing pawn from an empty tile")
        board.clear(actualTile)
      })
    }

    def clear(tile: Feature[G, B#Tile]): Action[G] =
      Action(s => s.changeBoard(_.clear(tile(s))))

    def moveFrom(tile: Feature[G, B#Tile]): MoveFromOp = MoveFromOp(tile)
    case class MoveFromOp(from: Feature[G, B#Tile]) {
      def to(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard { board =>
        val actualFrom = from(s)
        val movingPawn = board(actualFrom) getOrElse (throw new IllegalStateException("Moving a non present pawn"))
        board.clear(actualFrom).place(movingPawn, tile(s))
      })
    }

    def swap(tile: Feature[G, B#Tile]): SwapFromOp = SwapFromOp(tile)
    case class SwapFromOp(from: Feature[G, B#Tile]) {
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

    def replace(tile: Feature[G, B#Tile]): ReplaceOp = ReplaceOp(tile)
    case class ReplaceOp(tile: Feature[G, B#Tile]) {
      def using(action: B#Pawn => Feature[G, B#Pawn]): Action[G] = Action(s => s.changeBoard { board =>
        val actualTile = tile(s)
        val pawn = board(actualTile) getOrElse (throw new IllegalStateException("Replacing a non present pawn"))
        board.clear(actualTile).place(action(pawn)(s), actualTile)
      })
    }
  }

  def changeTurn[T](implicit ts: TurnState[T, G]): Action[G] = Action(g => ts.nextTurn(g))

  import Chainables._
  implicit object ChainableActions extends Chainable[Action[G], G, G] {
    override def chain(t1: Action[G], t2: Action[G]): Action[G] = unit(t1.run andThen t2.run)

    override def unit(f: G => G): Action[G] = Action(f)

    override def neutral: Action[G] = unit(g => g)

    override def transform(t: Action[G])(a: G): G = t.run(a)
  }
}
