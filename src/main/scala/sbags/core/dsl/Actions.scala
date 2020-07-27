package sbags.core.dsl

import sbags.core.extension._
import sbags.core.BoardStructure

case class Action[G](run: G => G) {
  def >>(other: Action[G]): Action[G] = Action(g => other.run(run(g)))
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
        if (!b(tile(s)).contains(pawn(s))) throw new IllegalStateException
        b.clear(tile(s))
      })
    }

    def clear(tile: Feature[G, B#Tile]): Action[G] =
      Action(s => s.changeBoard(_.clear(tile(s))))

    //def moveFrom()
  }

  def changeTurn[T](implicit ts: TurnState[T, G]): Action[G] = Action(g => ts.nextTurn(g))
}
