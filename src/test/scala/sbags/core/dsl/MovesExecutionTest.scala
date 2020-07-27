package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}

class MovesExecutionTest extends FlatSpec with Matchers {

  sealed trait Move

  case class BaseMove(moveName: String) extends Move

  case class Multiply2Move(moveName: String) extends Move

  private val add1: BaseMove = BaseMove("add1")
  private val genericMove: BaseMove = BaseMove("move")
  private val mul2: Multiply2Move = Multiply2Move("mul2")
  private val invalidMove: BaseMove = BaseMove("invalid")

  behavior of "MovesExecution"

  it should "be able to execute a move declared in its body" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove(add1)(_ + 1)
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be(1)
  }

  it should "be able to execute a move by matching" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove matching {
        case `add1` => _ + 1
      }
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be(1)
  }

  it should "be able to execute a move associated to a type" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove ofType[Multiply2Move] {
        _ * 2
      }
    }
    var state: Int = 1
    state = executionRules.collectMovesExecution(mul2)(state)
    state should be(2)
  }

  it should "execute only the first valid move in its body" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove(genericMove) {
        _ + 1
      }
      onMove matching {
        case `genericMove` => _ - 1
      }
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(genericMove)(state)
    state should be(1)
  }

  it should "do nothing if no valid move is present" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {}
    var state: Int = 0
    val initialState: Int = state
    state = executionRules.collectMovesExecution(invalidMove)(state)
    state should be(initialState)
  }

  it should "be possible to declare an action after each move" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove(add1) {
        _ + 1
      }
      after each move -> (_ + 1)
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be(2)
  }

  it should "be possible to declare an action that is executed before each move" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove(add1) {
        _ + 1
      }
      before each move -> (_ + 1)
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be(2)
  }

  it should "be possible to declare an action after each move of a type" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove(add1) {
        _ + 1
      }
      after each (move.ofType[BaseMove] -> (_ + 1))
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be(2)
  }

  it should "not execute an action if a move of the desired type is not executed" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove(add1) {
        _ + 1
      }
      after each move.ofType[Multiply2Move] -> (_ + 1)
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be(1)
  }

  it should "be possible to declare an action after each specific move" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove(add1) {
        _ + 1
      }
      after each move(add1) -> (_ + 1)
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be(2)
  }

  it should "not execute an action if a different move is executed" in {
    val executionRules: MovesExecution[Move, Int] = new MovesExecution[Move, Int] {
      onMove(add1) {
        _ + 1
      }
      after each move(invalidMove) -> (_ + 1)
    }
    var state: Int = 0
    state = executionRules.collectMovesExecution(add1)(state)
    state should be(1)
  }
}
