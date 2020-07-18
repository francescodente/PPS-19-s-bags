package sbags.core

import sbags.core.ruleset.RuleSet

/**
 * Represents the definition of a generic game, providing a factory to generate the initial state.
 */
trait GameDescription {
  type Move
  type State

  /**
   * Returns a new instance of the initial game state for this game description.
   * @return a new instance of the game state.
   */
  def newGame: Game[State, Move] = Game(initialState, ruleSet)

  protected def initialState: State

  val ruleSet: RuleSet[Move, State]
}
