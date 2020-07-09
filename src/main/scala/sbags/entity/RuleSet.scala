package sbags.entity

/**
 * Represents a definition of the rules for a game, namely the set of moves that are available
 * for any given state of the game. The rule set can be used to obtain a collection
 * of all available moves for a given game state or to verify that a move is valid.
 *
 * @tparam M the type of moves for this game
 * @tparam G the type of game state
 */
trait RuleSet[M, G] {
  /**
   * Returns true if the given move is valid for the implicitly defined game state.
   *
   * @param move the move to be checked.
   * @param state the state on which to check the move.
   * @return true if the move is valid, false otherwise.
   */
  def isValid(move: M)(implicit state: G): Boolean =
    availableMoves(state) contains move

  /**
   * Returns a sequence of all possible moves for the implicitly defined game state.
   *
   * @param state the state on which to calculate moves.
   * @return the sequence of generated moves.
   */
  def availableMoves(implicit state: G): Seq[M]

  def executeMove(move: M)(implicit state: G)
}
