package sbags.model.dsl

import org.scalatest.{FlatSpec, Matchers}
import sbags.model.dsl.Chainables.Chainable

class ModifiersTest extends FlatSpec with Matchers with Modifiers[Int] with Features[Int] {

  private val seqEvent = Seq(1, 2, 3)
  private val startValue = 0
  private val finalValue = seqEvent.map(_.toString).reduce(_ + _)

  case class IntGenerator(operation: Int => String) {
    def convert(value: Int): String = operation(value)
  }

  implicit val chainable: Chainable[IntGenerator, Int, String] = new Chainable[IntGenerator, Int, String] {
    override def chain(t1: IntGenerator, t2: IntGenerator): IntGenerator = IntGenerator(v => t1.convert(v) + t2.convert(v))

    override def unit(f: Int => String): IntGenerator = IntGenerator(f)

    override def neutral: IntGenerator = IntGenerator(_ => "")

    override def transform(t: IntGenerator)(a: Int): String = t.convert(a)
  }

  behavior of "a modifier"

  it should "be possible to iterating over events and collect them together" in {
    val t: IntGenerator = iterating over seqEvent as { t => IntGenerator(_ => t + "")}
    t.convert(startValue) should be (finalValue)
  }

  it should "return the neutral value if the seq is empty" in {
    val t: IntGenerator = iterating over Seq[Int]() as {_ => IntGenerator(x => (x + 1).toString)}
    t.convert(startValue) should be ("")
  }

  it should "be possible to filter the elements with where operator" in {
    val t = iterating over seqEvent where (_ <= 1) as {_ => IntGenerator(_.toString)}
    t.convert(startValue) should be ("0")
  }
}
