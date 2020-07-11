package sbags.entity

/**
 * Represents the definition of a generic game, providing a factory to generate the initial state.
 */
trait GameDescription[G <: GameState] {

  /**
   * Returns a new instance of the initial game state for this game description.
   * @return a new instance of the game state.
   */
  def newGame: G
}
