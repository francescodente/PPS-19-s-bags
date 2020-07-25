package sbags.core.dsl

import sbags.core.{BoardGameState, BoardStructure, PlacedPawn}
import sbags.core.BoardGameState._

case class Action[G](run: G => G) {
  def >>(other: Action[G]): Action[G] = Action(g => other.run(run(g)))
}

trait Actions[G] {
  def >[B <: BoardStructure](implicit ev: BoardGameState[B, G]): BoardActions[B] =
    new BoardActions

  implicit def actionToFunction(action: Action[G]): G => G = action.run

  class BoardActions[B <: BoardStructure](implicit ev: BoardGameState[B, G]) {
    def place(p: PlacedPawn[B#Tile, B#Pawn]): Action[G] =
      Action(_.changeBoard(_.place(p.pawn, p.tile)))

    def remove(p: PlacedPawn[B#Tile, B#Pawn]): Action[G] =
      Action(_.changeBoard { b =>
        if (!b(p.tile).contains(p.pawn)) throw new IllegalStateException
        b clear p.tile
      })

    def clear(t: B#Tile): Action[G] =
      Action(_.changeBoard(_.clear(t)))
  }

  def clear[B <: BoardStructure](t: B#Tile)(implicit ev: BoardGameState[B, G]): G => G =
    _.changeBoard(_.clear(t))
}
