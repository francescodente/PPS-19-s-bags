package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}

class MovesExecutionTest extends FlatSpec with Matchers {
  sealed trait Move
  case class BaseMove(moveName: String) extends Move
  case class Multiply2Move(moveName: String) extends Move
  private val add1: BaseMove = BaseMove("add1")
  private val move: BaseMove = BaseMove("move")
  private val mul2: Multiply2Move = Multiply2Move("mul2")
  private val invalidMove: BaseMove = BaseMove("invalid")

  behavior of "MovesExecution"

  it should "be able to execute a move declared in its body" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove (add1)(_ + 1)
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be (1)
  }

  it should "be able to execute a move by matching" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove matching {
        case `add1` => _ + 1
      }
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be (1)
  }

  it should "be able to execute a move associated to a type" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove ofType[Multiply2Move]  { _ * 2 }
    }
    var state: Int = 1
    state = executionRules.collectMovesExecution(mul2)(state)
    state should be (2)
  }

  it should "execute only the first valid move in its body" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove (move) {
        _ + 1
      }
      onMove matching {
        case `move` => _ - 1
      }
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(move)(state)
    state should be (1)
  }

  it should "do nothing if no valid move is present" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] { }
    var state: Int = 0
    val initialState: Int = state
    state = executionRules.collectMovesExecution(invalidMove)(state)
    state should be (initialState)
  }
}
