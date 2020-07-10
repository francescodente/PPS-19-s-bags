package sbags.entity

object Mocks {
  class TestState extends GameState {
    override type Move = Any
    override def executeMove(move: Any): Boolean = true
    override def ruleSet: RuleSet[Any, this.type] = ???
  }
}
