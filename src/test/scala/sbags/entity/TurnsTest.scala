package sbags.entity

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.entity.Mocks.TestState

class TurnsTest extends FlatSpec with Matchers with MockFactory {
  private def stateWithTurnStream(streamTest: Stream[Int]): TestState with TurnsStream[Int] =
    new TestState with TurnsStream[Int] {
      override var remainingTurns: Stream[Int] = streamTest
    }

  behavior of "A gameState with TurnsStream"

  it should "start with its initial turn" in {
    val turns = Stream(0,1)
    val gameState = stateWithTurnStream(turns)
    gameState.turn should be(Some(turns.head))
  }

  it should "go to the second turn after one turn is passed" in {
    val turns = Stream(0,1)
    val gameState = stateWithTurnStream(turns)
    gameState.nextTurn()
    gameState.turn should be(Some(turns(1)))
  }

  it should "have no more turns left at the end of the game" in {
    val turns = Stream(0)
    val gameState = stateWithTurnStream(turns)
    gameState.nextTurn()
    gameState.turn should be(None)
  }
}
