package sbags.core.dsl

import sbags.core.BoardGameState._
import sbags.core.{BoardGameState, BoardStructure, TurnState}

case class Action[G](run: G => G) {
  def >>(other: Action[G]): Action[G] = Action(g => other.run(run(g)))
}

trait Actions[G] {
  def >[B <: BoardStructure](implicit ev: BoardGameState[B, G]): BoardActions[B] =
    new BoardActions

  implicit def actionToFunction(action: Action[G]): G => G = action.run

  class BoardActions[B <: BoardStructure](implicit ev: BoardGameState[B, G]) {
    def place(pawn: B#Pawn): PlaceOp = PlaceOp(pawn)
    case class PlaceOp(pawn: B#Pawn) {
      def on(tile: B#Tile): Action[G] = Action(_.changeBoard(_.place(pawn, tile)))
    }

    def remove(pawn: B#Pawn): RemoveOp = RemoveOp(pawn)
    case class RemoveOp(pawn: B#Pawn) {
      def from(tile: B#Tile): Action[G] = Action(_.changeBoard { b =>
        if (!b(tile).contains(pawn)) throw new IllegalStateException
        b.clear(tile)
      })
    }

    def clear(t: B#Tile): Action[G] =
      Action(_.changeBoard(_.clear(t)))

    //def moveFrom()
  }

  def changeTurn[T](implicit ts: TurnState[T, G]): Action[G] = Action(g => ts.nextTurn(g))
}
