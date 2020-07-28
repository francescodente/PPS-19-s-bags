package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}
import sbags.core.{Board, BoardStructure}
import sbags.core.extension.{BoardState, TurnState}

object TestBoard extends BoardStructure {
  type Tile = Int
  type Pawn = String

  val pawn: Pawn = "a"
  val otherPawn: Pawn = "b"
  val tile: Tile = 0
  val otherTile: Tile = 1
  override def tiles: Seq[Tile] = Seq(tile, otherTile)
}

class ActionsTest extends FlatSpec with Matchers {
  behavior of "An action"

  it can "be concatenated to another action" in {
    val a1 = Action[String](_ + "abc")
    val a2 = Action[String](_ + "def")
    (a1 andThen a2).run("xyz") should be ("xyzabcdef")
  }

  behavior of "Board actions"
  implicit object TestBoardState extends BoardState[TestBoard.type, Board[TestBoard.type]] {
    override def boardState(state: Board[TestBoard.type]): Board[TestBoard.type] = state

    override def setBoard(state: Board[TestBoard.type])(board: Board[TestBoard.type]): Board[TestBoard.type] = board
  }

  they can "be used to place pawns" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard)
      private val newState = (> place TestBoard.pawn on TestBoard.tile).run(initialState)
      newState(TestBoard.tile) should be(Some(TestBoard.pawn))
    }
  }

  they can "be used to remove pawns" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (TestBoard.pawn, TestBoard.tile)
      private val newState = (> remove TestBoard.pawn from TestBoard.tile).run(initialState)
      newState(TestBoard.tile) should be(None)
    }
  }

  they should "fail removing a pawn when the actual pawn is different" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (TestBoard.otherPawn, TestBoard.tile)
      an[IllegalStateException] should be thrownBy {
        (> remove TestBoard.pawn from TestBoard.tile).run(initialState)
      }
    }
  }

  they can "be used to clear tiles" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (TestBoard.pawn, TestBoard.tile)
      private val newState = (> clear TestBoard.tile).run(initialState)
      newState(TestBoard.tile) should be(None)
    }
  }

  they can "be used to move pawns" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (TestBoard.pawn, TestBoard.tile)
      private val newState = (> moveFrom TestBoard.tile to TestBoard.otherTile).run(initialState)
      (newState(TestBoard.tile), newState(TestBoard.otherTile)) should be (None, Some(TestBoard.pawn))
    }
  }

  they can "be used to swap pawns" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (TestBoard.pawn, TestBoard.tile) place (TestBoard.otherPawn, TestBoard.otherTile)
      private val newState = (> swap TestBoard.tile and TestBoard.otherTile).run(initialState)
      (newState(TestBoard.tile), newState(TestBoard.otherTile)) should be (Some(TestBoard.otherPawn), Some(TestBoard.pawn))
    }
  }

  they can "be used to swap empty tiles" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard)
      private val newState = (> swap TestBoard.tile and TestBoard.otherTile).run(initialState)
      (newState(TestBoard.tile), newState(TestBoard.otherTile)) should be (None, None)
    }
  }

  they can "be used to swap one empty tile with a non empty tile" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (TestBoard.pawn, TestBoard.tile)
      private val newState = (> swap TestBoard.tile and TestBoard.otherTile).run(initialState)
      (newState(TestBoard.tile), newState(TestBoard.otherTile)) should be (None, Some(TestBoard.pawn))
    }
  }

  they can "be used to replace a pawn" in {
    new Actions[Board[TestBoard.type]] with Features[Board[TestBoard.type]] {
      private val initialState = Board(TestBoard) place (TestBoard.pawn, TestBoard.tile)
      private val newState = (> replace TestBoard.tile using (_ => TestBoard.otherPawn)).run(initialState)
      newState(TestBoard.tile) should be (Some(TestBoard.otherPawn))
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
