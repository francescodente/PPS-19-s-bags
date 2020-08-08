package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}
import sbags.core.dsl.Chainables._

class MovesGenerationTest extends FlatSpec with Matchers with Generators[String, String]{
  private val state = "xyz"

  behavior of "A move generator"

  it should "generate no moves when created with no rules" in {
    val gen = new MovesGeneration[String, String]{}
    gen.generateMoves(state) should be (empty)
  }

  it should "generate the moves specified in a single move generation statement" in {
    val gen = new MovesGeneration[String, String] {
      moveGeneration { s => s.toSeq.map("" + _) }
    }
    gen.generateMoves(state) should contain theSameElementsAs Seq("x","y","z")
  }

  it should "generate the union of moves defined in different move generation statements" in {
    val gen = new MovesGeneration[String, String] {
      moveGeneration { s => s.toSeq.take(2).map("" + _)}
      moveGeneration { s => s.toSeq.drop(2).map("" + _)}
    }
    gen.generateMoves(state) should contain theSameElementsAs Seq("x","y","z")
  }

}
