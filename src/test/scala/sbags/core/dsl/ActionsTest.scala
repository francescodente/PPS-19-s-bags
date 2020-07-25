package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}
import sbags.core.{Board, BoardGameState, BoardStructure}
import sbags.core._

object TestBoard extends BoardStructure {
  type Tile = Int
  type Pawn = String

  val pawn: Pawn = "a"
  val otherPawn: Pawn = "b"
  val tile: Tile = 0
  override def tiles: Seq[Tile] = Seq(tile)
}

class ActionsTest extends FlatSpec with Matchers with Actions[Board[TestBoard.type]] {
  behavior of "An action"

  it can "be concatenated to another action" in {
    val a1 = Action[String](_ + "abc")
    val a2 = Action[String](_ + "def")
    (a1 >> a2).run("xyz") should be ("xyzabcdef")
  }

  implicit object TestBoardState extends BoardGameState[TestBoard.type, Board[TestBoard.type]] {
    override def boardState(state: Board[TestBoard.type]): Board[TestBoard.type] = state

    override def setBoard(state: Board[TestBoard.type])(board: Board[TestBoard.type]): Board[TestBoard.type] = board
  }

  behavior of "Board actions"

  they can "be used to place pawns" in {
    val state = Board(TestBoard)
    val newState = (> place (TestBoard.pawn on TestBoard.tile)).run(state)
    newState(TestBoard.tile) should be (Some(TestBoard.pawn))
  }

  they can "be used to remove pawns" in {
    val state = Board(TestBoard) place (TestBoard.pawn, TestBoard.tile)
    val newState = (> remove (TestBoard.pawn from TestBoard.tile)).run(state)
    newState(TestBoard.tile) should be (None)
  }

  they should "fail removing a pawn when the actual pawn is different" in {
    val state = Board(TestBoard) place (TestBoard.otherPawn, TestBoard.tile)
    an [IllegalStateException] should be thrownBy {
      (> remove (TestBoard.pawn from TestBoard.tile)).run(state)
    }
  }

  they can "be used to clear tiles" in {
    val state = Board(TestBoard) place (TestBoard.pawn, TestBoard.tile)
    val newState = (> clear TestBoard.tile).run(state)
    newState(TestBoard.tile) should be (None)
  }
}
