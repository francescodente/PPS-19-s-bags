package sbags.entity

object Mocks {

  trait DefaultTestState extends GameState {
    type Move = Any
    type Rules = RuleSet[Any, this.type]

    override def ruleSet: Rules = ???
  }

  class TestState extends DefaultTestState
}
