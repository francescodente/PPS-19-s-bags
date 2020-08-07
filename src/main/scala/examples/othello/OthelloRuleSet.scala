package examples.othello

import sbags.core.ruleset.RuleSet
import examples.othello.Othello._
import sbags.core.dsl.RuleSetBuilder

object OthelloRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  moveGeneration {
    iterating over emptyTiles as { t =>
      generate (Put(t))
    }
  }
}
