package sbags.model.core

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class GameTest extends FlatSpec with MockFactory with Matchers {
  type MoveMock = Int
  type StateMock = Int

  private val ruleSetMock = mock[RuleSet[MoveMock, StateMock]]
  private val initState: StateMock = 0
  private val move1: MoveMock = 1
  private val move2: MoveMock = 2

  private def newGameTest: Game[MoveMock, StateMock] = Game(initState, ruleSetMock)

  behavior of "A Game"

  it should "have the state passed in constructor" in {
    newGameTest.currentState should be(initState)
  }

  it should "have the new state if a valid move is executed" in {
    val newState = 1
    (ruleSetMock.isValid(_: MoveMock)(_: StateMock)).expects(move1, initState).returns(true)
    (ruleSetMock.executeMove(_: MoveMock)(_: StateMock)).expects(move1, initState).returns(newState)
    val game = newGameTest
    game executeMove move1 should be(Right(newState))
  }

  it should "fail if it tries an invalid Move" in {
    (ruleSetMock.isValid(_: MoveMock)(_: StateMock)).expects(move1, initState).returns(false)
    newGameTest.executeMove(move1) should be(Left(InvalidMove))
  }

  it should "fail if it tries a valid Move but something goes wrong in execution" in {
    val exception = new IllegalStateException
    (ruleSetMock.isValid(_: MoveMock)(_: StateMock)).expects(move1, initState).returns(true)
    (ruleSetMock.executeMove(_: MoveMock)(_: StateMock)).expects(move1, initState).throws(exception)
    newGameTest.executeMove(move1) should be(Left(Error(exception)))
  }

  it should "not undo a move in no moves were previously made" in {
    newGameTest.undoLastMove() should be(None)
  }

  it can "undo moves if any move was previously made" in {
    val newState = 1
    (ruleSetMock.isValid(_: MoveMock)(_: StateMock)).expects(move1, initState).returns(true)
    (ruleSetMock.executeMove(_: MoveMock)(_: StateMock)).expects(move1, initState).returns(newState)
    val game = newGameTest
    game.executeMove(move1)
    game.undoLastMove() should be(Some(initState))
  }

  it can "undo multiple moves" in {
    (ruleSetMock.isValid(_: MoveMock)(_: StateMock)).expects(*, *).returns(true).anyNumberOfTimes()
    (ruleSetMock.executeMove(_: MoveMock)(_: StateMock)).expects(*, *).onCall((m, s) => s + m).anyNumberOfTimes()
    val game = newGameTest
    game.executeMove(move1)
    game.executeMove(move2)
    game.undoLastMove()
    game.undoLastMove() should be(Some(initState))
  }

  it should "execute moves after an undo" in {
    (ruleSetMock.isValid(_: MoveMock)(_: StateMock)).expects(*, *).returns(true).anyNumberOfTimes()
    (ruleSetMock.executeMove(_: MoveMock)(_: StateMock)).expects(*, *).onCall((m, s) => s + m).anyNumberOfTimes()
    val game = newGameTest
    game.executeMove(move1)
    game.undoLastMove()
    game.executeMove(move2) should be(Right(initState + move2))
  }
}
