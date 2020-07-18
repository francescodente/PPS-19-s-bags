package sbags.core

import examples.tictactoe.{Put, TicTacToeBoard, TicTacToeMove, TicTacToePawn, TicTacToeState, X}
import sbags.core.ruleset.RuleSetBuilder

package object dsl {
//  trait RuleSetDsl[M, B <: BoardStructure, G <: BoardGameState[B]] {
//    protected val builder: RuleSetBuilder[M, G] = new RuleSetBuilder[M, G]
//
//    object OnMove {
//      def apply(m: M): (G => G) => Unit =
//        g => builder.addMoveExe { case `m` => g }
//
//      def matching(f: PartialFunction[M, G => G]): Unit =
//        builder.addMoveExe(f)
//    }
//
//    protected val onMove: OnMove.type = OnMove
//
//    val place: B#Pawn => G => G = ???
//
//    implicit class IsPlacedOn(pawn: B#Pawn) {
//      def isPlacedOn(tile: B#Tile): Board[B] => Board[B] =
//        b => b place (pawn, tile)
//    }
//  }
//
//  object XX extends RuleSetDsl[TicTacToeMove, TicTacToeBoard.type, TicTacToeState] {
//    onMove matching {
//      case Put(t) =>
//        state => TicTacToeState((state.currentTurn isPlacedOn t)(state.board), TicTacToePawn.opponent(state.currentTurn))
//    }
//  }
}
