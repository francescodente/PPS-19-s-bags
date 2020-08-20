package sbags.model.dsl

case class Generator[M, G](generate: G => Seq[M])

/**
 * Enables words to work with the moves generator.
 *
 * @tparam M type of moves.
 * @tparam G type of the game state.
 */
trait Generators[M, G] {

  /**
   * Simple move generator.
   *
   * @param m move to be generate.
   * @return a generator that generate always the move m.
   */
  def generate(m: M*): Generator[M, G] = Generator(_ => m)

  /** A generator that generate no move. */
  def nothing: Generator[M, G] = generate()

  /** Implicit conversion from a Generator[M, G] to a function from G to Seq[M]. */
  implicit def generatorToFunction(g: Generator[M, G]): G => Seq[M] = g.generate

  /** Implicit conversion from a function G to Seq[M] to a Generator[M, G]. */
  implicit def functionToGenerator(f: G => Seq[M]): Generator[M, G] = Generator(f)

  import Chainables._
  /** Implicit chainable generators. */
  implicit object ChainableGenerators extends Chainable[Generator[M, G], G, Seq[M]] {
    override def chain(t1: Generator[M, G], t2: Generator[M, G]): Generator[M, G] =
      unit(g => t1.generate(g) ++ t2.generate(g))

    override def unit(f: G => Seq[M]): Generator[M, G] = Generator(f)

    override def neutral: Generator[M, G] = unit(_ => Seq.empty)

    override def transform(t: Generator[M, G])(a: G): Seq[M] = t.generate(a)
  }
}
