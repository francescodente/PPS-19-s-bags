package sbags.core

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.core.ruleset.{RuleSet, RuleSetBuilder}

class RuleSetBuilderTest extends FlatSpec with Matchers with MockFactory {

  def newBuilder = new RuleSet[Int, String] with RuleSetBuilder[Int, String]

  behavior of "a ruleSet builder"

  it should "be able to add a move generator" in {
    "newBuilder.addMoveGen(s => 0 to s.length)" should compile
  }

  it should "use an added gen to generates moves" in {
    val ruleSetB = newBuilder
    ruleSetB addMoveGen (s => 0 to s.length)
    ruleSetB availableMoves "state" should contain theSameElementsAs Seq(0, 1, 2, 3, 4, 5)
  }

  it should "be able to add an execution for a move" in {
    "newBuilder.addMoveExe({case (move, s) => print(\"state is \" + s + \" move is \" + move)})" should compile
  }

  it should "execute the correct move" in {
    val ruleSetB = newBuilder
    val move: Int = 0
    val state: String = "State"
    ruleSetB addMoveGen (_ => Seq(move))

    val mockFun = mockFunction[(Int, String), Unit]

    val pf: PartialFunction[(Int, String), Unit] = {
      case t @ (`move`, `state`) => mockFun(t)
    }

    inSequence {
      mockFun.expects((move, state))
    }

    ruleSetB addMoveExe pf
    ruleSetB.executeMove(move)(state)
  }
}
