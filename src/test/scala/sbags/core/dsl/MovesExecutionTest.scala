package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}
import sbags.core.TurnState

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

  it should "be possible to change turn after each move" in {
    case class GameWithTurn(t: Boolean, state: Int)
    import TurnState._
    implicit val turnState: TurnState[Boolean, GameWithTurn] = new TurnState[Boolean, GameWithTurn] {
      override def turn(state: GameWithTurn): Boolean = state.t

      override def nextTurn(state: GameWithTurn): GameWithTurn = state.copy(t = !state.t)
    }
    val executionRules: MovesExecution[Move, GameWithTurn] = new MovesExecution[Move, GameWithTurn] {
      onMove(add1) {
        s => {
          s.copy(state = s.state + 1)
        }
      }
      after each move -> changeTurn
    }
    var game: GameWithTurn = GameWithTurn(t = true, 0)
    for (_ <- 0 until 3) game = executionRules.collectMovesExecution(add1)(game)
    // true -> false -> true -> false
    game.turn should be(false)
  }
}
