package sbags.core.dsl

trait MovesGeneration[M, G] {
  private var generators: List[GenerationContext => G => Unit] = List()

  implicit val generationAccumulator: Accumulator[Unit, G] = new Accumulator[Unit, G] {
    override def accumulate(fs: Seq[G => Unit]): G => Unit = g => fs.foreach(_(g))

    override def neutral(s: G): Unit = { }
  }

  def moveGeneration(g: GenerationContext => G => Unit): Unit = generators = generators :+ g

  def generateMoves(state: G): Stream[M] = {
    val ctx: GenerationContext = new GenerationContext(state)
    generators.foreach(_(ctx)(state))
    ctx.moves
  }

  class GenerationContext(val state: G) {
    private var movesStream: Stream[M] = Stream.empty
    def addMoves(g: Seq[M]): Unit = movesStream = g.toStream ++ movesStream
    def moves: Stream[M] = movesStream
  }

  def generate(m: M*)(implicit ctx: GenerationContext): G => Unit = _ => ctx.addMoves(m)
}
