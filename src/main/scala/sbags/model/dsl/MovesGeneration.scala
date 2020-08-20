package sbags.model.dsl

/**
 * Enables words to work with the moves generations.
 *
 * @tparam M type of moves.
 * @tparam G type of the game state.
 */
trait MovesGeneration[M, G] {
  private var generators: List[G => Seq[M]] = List()

  /**
   * Add a generator, this will be used to generate the valid sequence of moves.
   *
   * @param generator a function that create a Seq[M] from a state G.
   */
  def moveGeneration(generator: G => Seq[M]): Unit = generators = generators :+ generator

  /**
   * Uses all the generators added to create a Seq[M] that represent all the available moves for that particular state.
   *
   * @param state the state.
   * @return a Seq[M] with all the available moves for the given state.
   */
  def generateMoves(state: G): Seq[M] = {
    generators flatMap (_(state))
  }
}
