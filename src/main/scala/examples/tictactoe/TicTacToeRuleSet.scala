package examples.tictactoe

import examples.tictactoe.TicTacToe.{Move, State}
import sbags.model.dsl.RuleSetBuilder
import examples.tictactoe.TicTacToe._
import sbags.model.core.RuleSet

object TicTacToeRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  onMove matching {
    /** When the Put move is performed place the current player (currentTurn) on the target tile. */
    case Put(t) =>
      > place currentTurn on t
  }

  after each move -> changeTurn

  moveGeneration {
    /** Legal moves are Put on each of the tiles with no pawn on them */
    iterating over emptyTiles as { t =>
      generate (Put(t))
    }
  }
}
