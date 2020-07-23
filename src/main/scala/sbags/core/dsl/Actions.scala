package sbags.core.dsl

import sbags.core.{BoardGameState, BoardStructure}
import sbags.core.BoardGameState._

case class Action[G](run: G => G) {
  def >>(other: Action[G]): Action[G] = Action(g => other.run(run(g)))
}

trait Actions[G] {
  implicit class IsOperations[P](pawn: P) {
    def is[T](verb: P => T): T = verb(pawn)
  }

  def placed[B <: BoardStructure](implicit ev: BoardGameState[B, G]): B#Pawn => PlacedResult[B] =
    PlacedResult(_)

  case class PlacedResult[B <: BoardStructure](pawn: B#Pawn)(implicit ev: BoardGameState[B, G]) {
    def on(tile: B#Tile): Action[G] = Action(g => g.setBoard(g.boardState place (pawn, tile)))
  }
}
