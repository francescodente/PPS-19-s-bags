package sbags.core.dsl

trait MovesGeneration[M, G] {
  private var generators: List[GenerationContext => Unit] = List()

  def moveGeneration(g: GenerationContext => Unit): Unit = generators = generators :+ g

  def generateMoves(state: G): Stream[M] = {
    val ctx: GenerationContext = new GenerationContext(state)
    generators.foreach(_(ctx))
    ctx.moves
  }

  class GenerationContext(val state: G) {
    private var movesStream: Stream[M] = Stream.empty
    def addMoves(g: Seq[M]): Unit = movesStream = g.toStream ++ movesStream
    def moves: Stream[M] = movesStream
  }

  object iterating {
    def over[F](feature: Feature[G, Seq[F]]): Iteration[F] = Iteration(feature)

    case class Iteration[F](feature: Feature[G, Seq[F]]) {
      def as(action: F => Unit)(implicit ctx: GenerationContext): Unit =
        feature(ctx.state).foreach(action)

      def mappedTo[X](f: F => X): Iteration[X] = Iteration(feature map (_ map f))

      def where(f: F => Boolean): Iteration[F] = Iteration(feature map (_ filter f))
    }
  }

  def generate(m: M*)(implicit ctx: GenerationContext): Unit = ctx.addMoves(m)

  object when {
    def apply(predicate: G => Boolean)(action: => Unit)(implicit ctx: GenerationContext): Unit =
      if (predicate(ctx.state)) action
  }
}
