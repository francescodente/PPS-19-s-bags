package sbags.entity

object Mocks {

  trait DefaultTestState extends GameState {
    override type Move = Any
    override def executeMove(move: Any): Unit = {}
    override def ruleSet: RuleSet[Any, this.type] = ???
  }

  class TestState extends DefaultTestState
}
