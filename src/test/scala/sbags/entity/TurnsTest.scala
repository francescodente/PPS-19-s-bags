package sbags.entity

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.entity.Mocks.TestState

class TurnsTest extends FlatSpec with Matchers with MockFactory {

  behavior of "A gameState with TurnsStream"

  it should "has turn equal the head of Stream when created" in {
    val streamTest = Stream(0,1)
    val gameStateTurnsIteratorTest = stateWithTurnStream(streamTest)
    gameStateTurnsIteratorTest.turn should be(Some(streamTest.head))
  }

  private def stateWithTurnStream(streamTest: Stream[Int]) = {
    new TestState with TurnsStream[Int] {
      override var remainingTurns: Stream[Int] = streamTest
    }
  }

  it should "has turn equal the second value of Stream when created" in {
    val streamTest = Stream(0,1)
    val gameStateTurnsIteratorTest = stateWithTurnStream(streamTest)
    gameStateTurnsIteratorTest.nextTurn()
    gameStateTurnsIteratorTest.turn should be(Some(streamTest(1)))
  }

  it should "be None when match finishes (no more turn left)" in {
    val streamTest = Stream(0)
    val gameStateTurnsIteratorTest = stateWithTurnStream(streamTest)
    gameStateTurnsIteratorTest.nextTurn()
    gameStateTurnsIteratorTest.turn should be(None)
  }

}
