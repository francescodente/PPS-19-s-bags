package sbags.core.dsl

trait MovesGeneration[M, G] {
  private var generators: List[G => Seq[M]] = List()

  def moveGeneration(g: G => Seq[M]): Unit = generators = generators :+ g

  def generateMoves(state: G): Seq[M] = {
    generators flatMap (_(state))
  }
}
