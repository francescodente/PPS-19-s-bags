package sbags.core.dsl

import sbags.core.ruleset.RuleSet

trait RuleSetBuilder[M, G] extends MovesExecution[M, G] with MovesGeneration[M, G] with Features[G] { this: RuleSet[M, G] =>
  override def availableMoves(state: G): Seq[M] =
    generateMoves(state)

  override def executeMove(move: M)(state: G): G =
    collectMovesExecution(move)(state)
}
