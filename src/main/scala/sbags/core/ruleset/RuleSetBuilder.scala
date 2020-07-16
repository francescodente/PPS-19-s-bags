package sbags.core.ruleset

class RuleSetBuilder[M, G] extends RuleSet[M, G] {
  private var movesGen: List[G => Seq[M]] = List()
  private var movesExe: List[PartialFunction[M, G => G]] = List()

  def addMoveGen(gen: G => Seq[M]): Unit = movesGen = movesGen :+ gen

  def addMoveExe(moveExe: PartialFunction[M, G => G]): Unit = movesExe = movesExe :+ moveExe

  override def availableMoves(state: G): Seq[M] =
    for (gen <- movesGen; move <- gen(state)) yield move

  override def executeMove(move: M)(state: G): G =
    movesExe.reduce(_ orElse _)(move)(state)
}
