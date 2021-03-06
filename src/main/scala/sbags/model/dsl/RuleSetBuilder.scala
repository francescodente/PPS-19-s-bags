package sbags.model.dsl

import sbags.model.core.RuleSet

/**
 * A mixin for a RuleSet that includes all the available DSL features provided by the library.
 *
 * @tparam M type of moves.
 * @tparam G type of the game state.
 */
trait RuleSetBuilder[M, G] extends MovesExecution[M, G]
  with MovesGeneration[M, G]
  with Modifiers[G]
  with Actions[G]
  with Generators[M, G]
  with Features[G] { this: RuleSet[M, G] =>
  override def availableMoves(state: G): Seq[M] =
    generateMoves(state)

  override def executeMove(move: M)(state: G): G =
    collectMovesExecution(move)(state)
}
