package sbags.model.dsl

/**
 * Represents an entity that generates a collection of moves of type M for a given state of type G.
 *
 * @param generate the function used to generate moves.
 * @tparam M the type of moves.
 * @tparam G the type of the game state.
 */
case class Generator[M, G](generate: G => Seq[M])

/**
 * Groups utilities to work with moves generators and provide a simple syntax to use the most common ones.
 *
 * @tparam M the type of moves.
 * @tparam G the type of the game state.
 */
trait Generators[M, G] {

  /**
   * Returns a simple generator that always produces the given sequence of moves.
   *
   * @param m moves to be generated.
   * @return a generator that always generates the given moves.
   */
  def generate(m: M*): Generator[M, G] = Generator(_ => m)

  /** A generator that generates no moves. */
  def nothing: Generator[M, G] = generate()

  /** Implicit conversion from a Generator[M, G] to a function from G to Seq[M]. */
  implicit def generatorToFunction(g: Generator[M, G]): G => Seq[M] = g.generate

  /** Implicit conversion from a function G to Seq[M] to a Generator[M, G]. */
  implicit def functionToGenerator(f: G => Seq[M]): Generator[M, G] = Generator(f)

  import Chainables._

  /**
   * Defines a [[sbags.model.dsl.Chainables.Chainable]] instance to be used with [[sbags.model.dsl.Generator]]s,
   * that implements chaining returning a generator producing the moves from the left and the right operand together.
   */
  implicit object ChainableGenerators extends Chainable[Generator[M, G], G, Seq[M]] {
    override def chain(t1: Generator[M, G], t2: Generator[M, G]): Generator[M, G] =
      unit(g => t1.generate(g) ++ t2.generate(g))

    override def unit(f: G => Seq[M]): Generator[M, G] = Generator(f)

    override def neutral: Generator[M, G] = unit(_ => Seq.empty)

    override def transform(t: Generator[M, G])(a: G): Seq[M] = t.generate(a)
  }
}
