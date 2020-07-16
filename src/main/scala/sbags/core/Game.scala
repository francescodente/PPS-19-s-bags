package sbags.core

import sbags.core.ruleset.RuleSet

trait Game[G <: GameState, M] {
  def currentState: G

  def executeMove(move: M): Unit
}

object Game {
  def apply[G <: GameState, M](initialState: G, ruleSet: RuleSet[M, G]): Game[G, M] =
    new BasicGame(initialState, ruleSet)

  class BasicGame[G <: GameState, M](private var state: G, protected val ruleSet: RuleSet[M, G]) extends Game[G, M] {
    override def currentState: G = state

    override def executeMove(move: M): Unit = {
      if (!ruleSet.isValid(move)(state)) throw new IllegalStateException
      state = ruleSet.executeMove(move)(state)
    }
  }
}
