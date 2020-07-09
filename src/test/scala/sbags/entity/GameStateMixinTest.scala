package sbags.entity

import org.scalatest.{FlatSpec, Matchers}

class GameStateMixinTest extends FlatSpec with Matchers{
  type BoardTypeTest = BasicBoard {
    type Tile = Int
    type Pawn = String
  }

  type TurnState = TestState with Turns {
    type Turn = Int
  }

  private val board: BoardTypeTest = new BasicBoard {
    type Tile = Int
    type Pawn = String
    override def tiles: Seq[Int] = Seq.empty
  }

  class TestState(val b: BoardTypeTest) extends BasicGameState[BoardTypeTest](b) {
    override type Move = Any
    override def executeMove(move: Any): Unit = {}

    override def ruleSet: RuleSet[Any, this.type] = ???
  }

  behavior of "A gameState with Turns as Int"

  val gameStateTurnsTest: TurnState = new TestState(board) with Turns {
    override type Turn = Int
    var turn: Option[Int] = Some(0)
    override def nextTurn(): Unit = turn = Some(turn.get+1)
  }

  it should "be at turn 0 when created" in {
    gameStateTurnsTest.turn should be(Some(0))
  }

  it should "be at turn 1 after 1 turn" in {
    gameStateTurnsTest.nextTurn()
    gameStateTurnsTest.turn should be(Some(1))
  }

  behavior of "A gameState with TurnsStream"

  it should "has turn equal the head of Stream when created" in {
    val streamTest = Stream(0,1)
    val gameStateTurnsIteratorTest: TurnState = new TestState(board) with TurnsStream {
      override type Turn = Int
      override var remainingTurns: Stream[Int] = streamTest
    }
    gameStateTurnsIteratorTest.turn should be(Some(streamTest.head))
  }

  it should "has turn equal the second value of Stream when created" in {
    val streamTest = Stream(0,1)
    val gameStateTurnsIteratorTest: TurnState = new TestState(board) with TurnsStream {
      override type Turn = Int
      override var remainingTurns: Stream[Int] = streamTest
    }
    gameStateTurnsIteratorTest.nextTurn()
    gameStateTurnsIteratorTest.turn should be(Some(streamTest(1)))
  }

  it should "be None when match finishes (no more turn left)" in {
    val streamTest = Stream(0)
    val gameStateTurnsIteratorTest: TurnState = new TestState(board) with TurnsStream {
      override type Turn = Int
      override var remainingTurns: Stream[Int] = streamTest
    }
    gameStateTurnsIteratorTest.nextTurn()
    gameStateTurnsIteratorTest.turn should be(None)
  }

  behavior of "A gameState with GameEndConditions"
  val MAX_TURN = 2
  val gameStateTest: TestState with Turns with GameEndCondition[Boolean] {
    type Turn = Int
  } = new TestState(board) with Turns with GameEndCondition[Boolean] {
    override type Turn = Int
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
