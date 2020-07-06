package sbags.entity

/**
 * An operation that changes the current Game State and that comprises 0 or more modification of the Board State (e.g. castling);
 * @tparam S Defines the type of the Game State, with [[sbags.entity.BoardGameState]] as an upper-bound.
 */
trait Move[S <: BoardGameState[_]] {
  /**
   * Executes the move on a given Game State.
   * @param gameState on which the Move will be applied.
   * @return the new Game State.
   */
  def execute(gameState: S): S
}
