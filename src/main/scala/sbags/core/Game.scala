package sbags.core

import sbags.core.ruleset.RuleSet

trait Game[M, G] {
  def currentState: G
  def executeMove(move: M): Unit
}

object Game {
  def apply[M, G](initialState: G, ruleSet: RuleSet[M, G]): Game[M, G] =
    new BasicGame(initialState, ruleSet)

  class BasicGame[M, G](private var state: G, protected val ruleSet: RuleSet[M, G]) extends Game[M, G] {
    override def currentState: G = state

    override def executeMove(move: M): Unit = {
      if (!ruleSet.isValid(move)(state)) throw new IllegalStateException
      state = ruleSet.executeMove(move)(state)
    }
  }
}
