package sbags.model.core

import sbags.model.ruleset.RuleSet

/**
 * Represents the definition of a generic game, providing a factory to generate the initial state.
 */
trait GameDescription[M, G] {
  type Move = M
  type State = G

  /**
   * Returns a new instance of the initial game state for this game description.
   * @return a new instance of the game state.
   */
  def newGame: Game[M, G] = Game(initialState, ruleSet)

  protected def initialState: G

  val ruleSet: RuleSet[M, G]
}
