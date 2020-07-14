package sbags.core

import sbags.core.ruleset.RuleSet

object Mocks {
  trait DefaultTestState extends GameState {
    type Move = Any
    type Rules = RuleSet[Any, this.type]

    override def executeMove(move: Any): Unit = { }
    override def ruleSet: Rules = ???
  }

  class TestState extends DefaultTestState
}
