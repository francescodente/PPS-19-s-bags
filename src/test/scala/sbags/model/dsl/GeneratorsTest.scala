package sbags.model.dsl

import org.scalatest.{FlatSpec, Matchers}
import Chainables._

class GeneratorsTest extends FlatSpec with Matchers with Generators[String, String] {
  import ChainableGenerators._

  private val state = "xyz"
  private val moveA = "a"
  private val moveB = "b"
  private val moveC = "c"
  private val allMoves = Seq(moveA, moveB, moveC)

  behavior of "generators"

  they should "generate explicitly stated moves" in {
    generate (allMoves: _*).generate(state) should contain theSameElementsAs allMoves
  }

  they should "generate nothing when requested" in {
    nothing.generate(state) should contain theSameElementsAs Seq.empty
  }

  they should "generate the content of moves specified in a unit" in {
    unit(_ => Seq(moveA)).generate(state) should contain theSameElementsAs Seq(moveA)
  }

  they should "generate the union of moves specified in chained generators" in {
    val chained = unit(_ => Seq(moveA)) and unit(_ => Seq(moveB))
    chained.generate(state) should contain theSameElementsAs Seq(moveA, moveB)
  }

  they should "generate the moves of the first term when it is chained with neutral" in {
    val chained = unit(_ => Seq(moveA)) and neutral
    chained.generate(state) should contain theSameElementsAs Seq(moveA)
  }

  they should "generate the moves of the second term when neutral is chained with it" in {
    val chained = neutral and unit(_ => Seq(moveA))
    chained.generate(state) should contain theSameElementsAs Seq(moveA)
  }
}
