package sbags.core.ruleset

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class RuleSetBuilderTest extends FlatSpec with Matchers with MockFactory {
  type MoveMock = Int => Int
  type StateMock = Int

  private val defaultState: StateMock = 0
  private def defaultMoves: Seq[MoveMock] = Seq(_ + 1, _+2)

  private def newRuleSetBuilderTest: RuleSetBuilder[MoveMock, StateMock] = new RuleSetBuilder[MoveMock, StateMock]

  behavior of "A RuleSetBuilder"

  it should "be created" in {
    newRuleSetBuilderTest shouldBe a [RuleSet[MoveMock, StateMock]]
  }

  it should "be created with no available moves (should have to try with each possible state)" in {
    newRuleSetBuilderTest.availableMoves(defaultState) should contain theSameElementsAs Seq()
  }

  it should "be able to add move generator" in {
    val ruleSet = newRuleSetBuilderTest
    ruleSet.addMoveGen(_ => defaultMoves)
    ruleSet.availableMoves(defaultState) should contain theSameElementsAs defaultMoves
  }

  it should "be able to add an executable move" in {
    val ruleSet = newRuleSetBuilderTest
    val finalState: StateMock = 2
    ruleSet.addMoveExe({ case move: MoveMock if defaultMoves.contains(move) => _:StateMock => finalState})
  }

  it should "be able to use an executable move" in {
    val ruleSet = newRuleSetBuilderTest
    val finalState: StateMock = 2
    ruleSet.addMoveExe({ case move: MoveMock if defaultMoves.contains(move) => _:StateMock => finalState})
    ruleSet.executeMove(defaultMoves.head)(defaultState) should be (finalState)
  }

  it should "fail if doesn't exist any executable move" in {
    val ruleSet = newRuleSetBuilderTest
    val finalState: StateMock = 2
    an [UnsupportedOperationException] should be thrownBy {
      ruleSet.executeMove(defaultMoves.head)(defaultState) should be(finalState)
    }
  }
}
