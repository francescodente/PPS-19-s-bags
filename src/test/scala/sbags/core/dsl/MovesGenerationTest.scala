package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}

class MovesGenerationTest extends FlatSpec with Matchers {
  private val state = "xyz"
  private val moveA = "a"
  private val moveB = "b"
  private val moveC = "c"
  private val allMoves = Seq(moveA, moveB, moveC)
  private val chars = Feature[String, Seq[String]](_ map (_.toString))

  behavior of "A move generator"

  it should "generate no moves when created with no rules" in {
    val gen = new MovesGeneration[String, String] {
      moveGeneration { implicit context => _ => {} }
    }
    gen.generateMoves(state) should be (empty)
  }

  it should "generate explicit moves" in {
    val gen = new MovesGeneration[String, String] {
      moveGeneration { implicit context =>
        generate (allMoves: _*)
      }
    }
    gen.generateMoves(state) should contain theSameElementsAs allMoves
  }

  it should "generate moves when a condition is met" in {
    val gen = new MovesGeneration[String, String] with Modifiers[String] {
      moveGeneration { implicit context =>
        when (_ == state) {
          generate (moveA)
        }
      }
    }
    gen.generateMoves(state) should contain theSameElementsAs Seq(moveA)
  }

  it should "not generate moves when a condition is not met" in {
    val gen = new MovesGeneration[String, String] with Modifiers[String] {
      moveGeneration { implicit context =>
        when (_ != state) {
          generate (moveA)
        }
      }
    }
    gen.generateMoves(state) should be (empty)
  }

  it should "generate moves iteratively" in {
    val gen = new MovesGeneration[String, String] with Modifiers[String] {
      moveGeneration { implicit context =>
        iterating over chars as { c =>
          generate (c)
        }
      }
    }
    gen.generateMoves(state) should contain theSameElementsAs (state map (_.toString))
  }

  it should "generate moves with nested rules" in {
    val gen = new MovesGeneration[String, String] with Modifiers[String] {
      moveGeneration { implicit context =>
        iterating over chars as { c1 =>
          iterating over chars as { c2 =>
            generate (c1 + c2)
          }
        }
      }
    }
    val expected = for {
      m1 <- state
      m2 <- state
    } yield m1.toString + m2.toString
    gen.generateMoves(state) should contain theSameElementsAs expected
  }
}
