package sbags.core.ruleset

trait RuleSetBuilder[M, G] extends RuleSet[M, G] {
  private var movesGen: List[G => Seq[M]] = List()
  private var movesExe: List[PartialFunction[(M, G), G]] = List()

  def addMoveGen(gen: G => Seq[M]): Unit = movesGen = gen :: movesGen

  def addMoveExe(moveExe: PartialFunction[(M, G), G]): Unit = movesExe = moveExe :: movesExe

  override def availableMoves(implicit state: G): Seq[M] =
    for (gen <- movesGen; move <- gen(state)) yield move

  override def executeMove(move: M)(implicit state: G): G =
    movesExe reduce (_ orElse _) apply(move, state)
}
