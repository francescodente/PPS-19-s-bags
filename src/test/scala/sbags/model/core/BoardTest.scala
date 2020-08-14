package sbags.model.core

import org.scalatest.{FlatSpec, Matchers}

class BoardTest extends FlatSpec with Matchers {
  private val tilePosition: Int = 0

  object TestBoard extends BoardStructure {
    type Tile = Int
    type Pawn = String

    override def tiles: Seq[Tile] = List(tilePosition)
  }

  private def newSimpleBoard: Board[TestBoard.type] = Board(TestBoard)

  behavior of "A board"

  it should "not have any pawn in an initialize tile" in {
    val board = newSimpleBoard
    board(tilePosition) should be (None)
  }

  it can "add a pawn in a tile" in {
    val pawnName: String = "pawnName"
    val board = newSimpleBoard place (pawnName, tilePosition)
    board(tilePosition) should be (Some(pawnName))
  }

  it should "be initialized with a non-empty tile" in {
    val pawnName: String = "pawnName"
    val board = Board[TestBoard.type](Map(tilePosition -> pawnName))(TestBoard)
    board(tilePosition) should be (Some(pawnName))
  }

  it should "not allow placing a pawn on a non-empty tile" in {
    an [IllegalStateException] should be thrownBy {
      newSimpleBoard place ("pawnName1", tilePosition) place ("pawnName2", tilePosition)
    }
  }

  it can "remove a pawn from a tile" in {
    val board = newSimpleBoard place ("pawnName", tilePosition) clear tilePosition
    board(tilePosition) should be (None)
  }

  it should "not allow removing from an empty tile" in {
    an [IllegalStateException] should be thrownBy {
      newSimpleBoard clear tilePosition
    }
  }

  it should "be able to provide a board map" in {
    val pawnValue = "pawnName"
    val board = newSimpleBoard place (pawnValue, tilePosition)
    board.boardMap should contain (tilePosition -> pawnValue)
  }
}
