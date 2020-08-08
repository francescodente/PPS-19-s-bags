package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}
import Chainables._

class GeneratorsTest extends FlatSpec with Matchers with Generators[String, String] {
  import ChainableGenerators._

  private val state = "xyz"
  private val moveA = "a"
  private val moveB = "b"
  private val moveC = "c"
  private val allMoves = Seq(moveA, moveB, moveC)
  private val chars = Feature[String, Seq[String]](_ map (_.toString))

  behavior of "generators"

  they should "generate explicitly stated moves" in {
    generate (allMoves: _*).generate(state) should contain theSameElementsAs allMoves
  }

  they should "generate nothing when requested" in {
    nothing.generate(state) should be (empty)
  }

  they should "generate the union of moves specified in chained generators" in {
    val chained = unit(_ => Seq(moveA)) and unit(_ => Seq(moveB))
    chained.generate(state) should contain theSameElementsAs Seq(moveA, moveB)
  }
}
