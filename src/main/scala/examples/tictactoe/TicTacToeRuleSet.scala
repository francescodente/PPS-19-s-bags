package examples.tictactoe

import examples.tictactoe.TicTacToe.{Move, State}
import sbags.core.dsl.RuleSetBuilder
import sbags.core.ruleset.RuleSet
import examples.tictactoe.TicTacToe._

object TicTacToeRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  onMove matching {
    case Put(t) =>
      > place currentTurn on t
  }

  after each move -> changeTurn

  moveGeneration {
    iterating over emptyTiles as { t =>
      generate (Put(t))
    }
  }
}