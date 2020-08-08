package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}
import sbags.core.{Board, BoardStructure}
import sbags.core.extension.{BoardState, TurnState}
import sbags.core.dsl.Chainables._

object TestBoard extends BoardStructure {
  type Tile = Int
  type Pawn = String

  val pawnA: Pawn = "a"
  val pawnB: Pawn = "b"
  val tile0: Tile = 0
  val tile1: Tile = 1
  override def tiles: Seq[Tile] = Seq(tile0, tile1)
}

class ActionsTest extends FlatSpec with Matchers {
  behavior of "An action"

  it can "be concatenated to another action" in {
    val a1 = Action[String](_ + "abc")
    val a2 = Action[String](_ + "def")
    new Actions[String] {
      (a1 and a2).run("xyz") should be ("xyzabcdef")
    }
  }

  it should "not change the state when 'doNothing' action is run" in {
    new Actions[String] {
      doNothing.run("xyz") should be ("xyz")
    }
  }

  behavior of "Board actions"
  implicit object TestBoardState extends BoardState[TestBoard.type, Board[TestBoard.type]] {
    override def boardState(state: Board[TestBoard.type]): Board[TestBoard.type] = state

    override def setBoard(state: Board[TestBoard.type])(board: Board[TestBoard.type]): Board[TestBoard.type] = board
  }

  import TestBoard._
  they can "be used to place pawns" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard)
      private val newState = (> place pawnA on tile0).run(initialState)
      newState(tile0) should be(Some(pawnA))
    }
  }

  they can "be used to remove pawns" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (pawnA, tile0)
      private val newState = (> remove pawnA from tile0).run(initialState)
      newState(tile0) should be(None)
    }
  }

  they should "fail removing a pawn when the actual pawn is different" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (pawnB, tile0)
      an[IllegalStateException] should be thrownBy {
        (> remove pawnA from tile0).run(initialState)
      }
    }
  }

  they can "be used to clear tiles" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (pawnA, tile0)
      private val newState = (> clear tile0).run(initialState)
      newState(tile0) should be(None)
    }
  }

  they can "be used to move pawns" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (pawnA, tile0)
      private val newState = (> moveFrom tile0 to tile1).run(initialState)
      (newState(tile0), newState(tile1)) should be (None, Some(TestBoard.pawnA))
    }
  }

  they can "be used to swap pawns" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (pawnA, tile0) place (pawnB, tile1)
      private val newState = (> swap tile0 and tile1).run(initialState)
      (newState(tile0), newState(tile1)) should be (Some(pawnB), Some(pawnA))
    }
  }

  they can "be used to swap empty tiles" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard)
      private val newState = (> swap tile0 and tile1).run(initialState)
      (newState(tile0), newState(tile1)) should be (None, None)
    }
  }

  they can "be used to swap one empty tile with a non empty tile" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (pawnA, tile0)
      private val newState = (> swap tile0 and tile1).run(initialState)
      (newState(tile0), newState(tile1)) should be (None, Some(pawnA))
    }
  }

  they can "be used to replace a pawn" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (pawnA, tile0)
      private val newState = (> replace tile0 using (_ => pawnB)).run(initialState)
      newState(tile0) should be (Some(pawnB))
    }
  }

  behavior of "Turn Actions"
  implicit object TestTurns extends TurnState[Boolean, Int] {
    override def turn(state: Int): Boolean = state % 2 == 0

    override def nextTurn(state: Int): Int = state + 1
  }

  they can "be used to change turn" in {
    new Actions[Int] {
      private val initialState = 10
      changeTurn.run(initialState) should be (initialState + 1)
    }
  }
}
