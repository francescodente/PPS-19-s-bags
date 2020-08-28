package examples.tictactoe

import examples.tictactoe.TicTacToe.{Move, State, _}
import sbags.model.core.RuleSet
import sbags.model.dsl.RuleSetBuilder

object TicTacToeRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  onMove matching {
    /** When the Put move is performed place the current player (currentTurn) on the target tile. */
    case Put(t) =>
      > place activePlayer on t
  }

  after eachMove changeTurn

  moveGeneration {
    /** Legal moves are Put on each of the tiles with no pawn on them */
    iterating over emptyTiles as { t =>
      generate(Put(t))
    }
  }
}
