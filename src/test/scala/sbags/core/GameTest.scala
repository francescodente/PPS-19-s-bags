package sbags.core

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.core.ruleset.RuleSet

class GameTest extends FlatSpec with MockFactory with Matchers {
  type MoveMock = Int => Int
  type StateMock = Int

  private val ruleSetMock = mock[RuleSet[MoveMock, StateMock]]
  private val initState: StateMock = 0
  private def defaultMove: MoveMock = _ + 1

  private def newGameTest: Game[MoveMock, StateMock] = Game(initState, ruleSetMock)

  behavior of "A Game"

  it should "be created" in {
    Game(initState, ruleSetMock) shouldBe a [Game[_, _]]
  }

  it should "have the state passed in constructor" in {
    newGameTest.currentState should be (initState)
  }

  it should "have the new state if tries a valid move" in {
    val newState = 1
    (ruleSetMock.isValid(_:MoveMock)(_:StateMock)).expects(*,*).returns(true).once()
    (ruleSetMock.executeMove(_:MoveMock)(_:StateMock)).expects(*,*).returns(newState).once()
    val game = newGameTest
    game executeMove defaultMove
    game.currentState should be (newState)
  }

  it should "fail if it tries an invalid Move" in {
    (ruleSetMock.isValid(_:MoveMock)(_:StateMock)).expects(*,*).returns(false).once()
    a [IllegalStateException] should be thrownBy {
      newGameTest.executeMove(defaultMove)
    }
  }
}
