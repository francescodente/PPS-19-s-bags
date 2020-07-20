package sbags.core

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.core.ruleset.RuleSet

class GameDescriptionTest extends FlatSpec with MockFactory with Matchers {
  type MoveMock = Int => Int
  type StateMock = Int

  behavior of "A GameDescription"

  it should "be able to create a new game" in {
    val initState: StateMock = 1
    new GameDescription {
      override type Move = MoveMock
      override type State = StateMock

      override protected def initialState: State = initState

      override val ruleSet: RuleSet[MoveMock, StateMock] = mock[RuleSet[MoveMock, StateMock]]
    }.newGame shouldBe a [Game[_, _]]
  }
}
