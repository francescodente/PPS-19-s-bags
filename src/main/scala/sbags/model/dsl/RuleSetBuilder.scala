package sbags.model.dsl

import sbags.model.core.RuleSet

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
