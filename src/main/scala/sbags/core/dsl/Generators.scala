package sbags.core.dsl

case class Generator[M, G](generate: G => Seq[M])

trait Generators[M, G] {
  def generate(m: M*): Generator[M, G] = Generator(_ => m)

  def nothing: Generator[M, G] = generate()

  implicit def generatorToFunction(g: Generator[M, G]): G => Seq[M] = g.generate
  implicit def functionToGenerator(f: G => Seq[M]): Generator[M, G] = Generator(f)

  import Chainables._
  implicit object ChainableGenerators extends Chainable[Generator[M, G], G, Seq[M]] {
    override def chain(t1: Generator[M, G], t2: Generator[M, G]): Generator[M, G] =
      unit(g => t1.generate(g) ++ t2.generate(g))

    override def unit(f: G => Seq[M]): Generator[M, G] = Generator(f)

    override def neutral: Generator[M, G] = unit(_ => Seq.empty)

    override def transform(t: Generator[M, G])(a: G): Seq[M] = t.generate(a)
  }
}
