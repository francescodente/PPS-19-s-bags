package sbags.model.core

/** Represents the definition of a generic game, providing a factory to generate the initial state.
 *
 * @tparam M the type of moves for this game.
 * @tparam G the type of game state.
 */
trait GameDescription[M, G] {
  /** Represents the alias for the type of the moves. */
  type Move = M

  /** Represents the alias for the type of the game state. */
  type State = G

  /** Returns a new instance of the initial game state for this game description. */
  def newGame: Game[M, G] = Game(initialState, ruleSet)

  /** Returns the initial game state used to create the game. */
  protected def initialState: G

  /** Defines the [[sbags.model.core.RuleSet]] used to create the game. */
  val ruleSet: RuleSet[M, G]
}
