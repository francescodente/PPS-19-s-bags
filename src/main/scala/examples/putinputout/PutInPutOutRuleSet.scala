package examples.putinputout

import examples.putinputout.PutInPutOut._
import sbags.model.core.RuleSet
import sbags.model.dsl.RuleSetBuilder

/**
 * Defines the rule set of the PutInPutOut game, which allows to place ThePawn only when TheTile is empty
 * and to remove it only when TheTile is occupied.
 */
object PutInPutOutRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  onMove (PutIn) {
    > place ThePawn on TheTile
  }
  onMove (PutOut) {
    > clear TheTile
  }

  import sbags.model.dsl.Chainables._

  moveGeneration {
    when (TheTile is empty) {
      generate (PutIn)
    } and
      when (TheTile isNot empty) {
        generate (PutOut)
      }
  }
}
