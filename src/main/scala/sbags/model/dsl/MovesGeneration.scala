package sbags.model.dsl

/**
 * Enables syntax to construct moves generation for a rule set.
 *
 * @tparam M type of moves.
 * @tparam G type of the game state.
 */
trait MovesGeneration[M, G] {
  private var generators: List[G => Seq[M]] = List()

  /**
   * Adds a new generation rule, this will be used to generate the valid sequence of moves.
   *
   * @param generator a function that creates a sequence of moves from a state.
   */
  def moveGeneration(generator: G => Seq[M]): Unit = generators = generators :+ generator

  /**
   * Uses all the previously added generators to create a collection that represent all the available moves for
   * that particular state.
   *
   * @param state the state.
   * @return a collection with all the available moves for the given state.
   */
  def generateMoves(state: G): Seq[M] = {
    generators flatMap (_ (state))
  }
}
