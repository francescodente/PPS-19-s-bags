package sbags.core.dsl

import sbags.core.extension._
import sbags.core.BoardStructure

case class Action[G](run: G => G) {
  def andThen(other: Action[G]): Action[G] = Action(g => other.run(run(g)))
}

trait Actions[G] {
  def >[B <: BoardStructure](implicit ev: BoardState[B, G]): BoardActions[B] =
    new BoardActions

  implicit def actionToFunction(action: Action[G]): G => G = action.run

  class BoardActions[B <: BoardStructure](implicit ev: BoardState[B, G]) {
    def place(pawn: Feature[G, B#Pawn]): PlaceOp = PlaceOp(pawn)
    case class PlaceOp(pawn: Feature[G, B#Pawn]) {
      def on(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard(_.place(pawn(s), tile(s))))
    }

    def remove(pawn: Feature[G, B#Pawn]): RemoveOp = RemoveOp(pawn)
    case class RemoveOp(pawn: Feature[G, B#Pawn]) {
      def from(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard { b =>
        val realTile = tile(s)
        if (!b(realTile).contains(pawn(s))) throw new IllegalStateException
        b.clear(realTile)
      })
    }

    def clear(tile: Feature[G, B#Tile]): Action[G] =
      Action(s => s.changeBoard(_.clear(tile(s))))

    def moveFrom(tile: Feature[G, B#Tile]): MoveFromOp = MoveFromOp(tile)

    case class MoveFromOp(from: Feature[G, B#Tile]) {
      def to(tile: Feature[G, B#Tile]): Action[G] = Action(s => s.changeBoard { b =>
        val realFrom = from(s)
        val movingPawn = b(realFrom) getOrElse (throw new IllegalStateException)
        b.clear(realFrom).place(movingPawn, tile(s))
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
      def using(action: B#Pawn => Feature[G, B#Pawn]): Action[G] = Action(s => s.changeBoard {b =>
        val t = tile(s)
        val pawnOld = b(t) getOrElse (throw new IllegalStateException)
        b.clear(t).place(action(pawnOld)(s), t)
      })
    }
  }

  def changeTurn[T](implicit ts: TurnState[T, G]): Action[G] = Action(g => ts.nextTurn(g))
}
