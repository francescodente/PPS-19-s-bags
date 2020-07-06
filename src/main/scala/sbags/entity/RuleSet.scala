package sbags.entity

trait RuleSet[M] {
  def isValid(move: M): Boolean
  def availableMoves: Seq[M]
}
