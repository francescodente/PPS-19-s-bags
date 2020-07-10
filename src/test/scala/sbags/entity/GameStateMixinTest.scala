package sbags.entity

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class GameStateMixinTest extends FlatSpec with Matchers with MockFactory {

  class TestState extends GameState {
    override type Move = Any
    override def executeMove(move: Any): Boolean = true
    override def ruleSet: RuleSet[Any, this.type] = ???
  }

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

  behavior of "A gameState with GameEndConditions"
  val MAX_TURN = 2
  private val gameStateTest = new TestState with Turns[Int] with GameEndCondition[Boolean] {
    var turn: Option[Int] = Some(0)
    override def nextTurn(): Unit = turn = Some(turn.get+1)
    override def gameResult: Option[Boolean] = Some(true) filter (_ => turn.get > MAX_TURN)
  }

  it should "not be ended until condition is false" in {
    gameStateTest.gameResult should be(None)
  }

  it should "end when condition becomes true (turns are greater than " + MAX_TURN + ")" in {
    (0 to MAX_TURN).foreach(_ => {
      gameStateTest.gameResult should be(None)
      gameStateTest.nextTurn()
    })
    gameStateTest.gameResult should be(Some(true))
  }

}
