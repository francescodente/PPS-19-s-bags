package sbags.core

import org.scalatest.{FlatSpec, Matchers}

class BoardTest extends FlatSpec with Matchers {
  type TestBoard = BasicBoard {
    type Tile = Int
    type Pawn = String
  }

  private val tilePosition: Int = 0

  private def newSimpleBoard: TestBoard = new BasicBoard {
    type Tile = Int
    type Pawn = String
    override def tiles: Seq[Int] = List(tilePosition)
  }

  behavior of "A board"

  it should "not have any pawn in an initialize tile" in {
    val board = newSimpleBoard
    board(tilePosition) should be (None)
  }

  it can "add a pawn in a tile" in {
    val board = newSimpleBoard
    val pawnName: String = "pawnName"
    board << (pawnName -> tilePosition)
    board(tilePosition) should be (Some(pawnName))
  }

  it should "not allow placing a pawn on a non-empty tile" in {
    an [IllegalStateException] should be thrownBy {
      val board = newSimpleBoard
      board << ("pawnName1" -> tilePosition)
      board << ("pawnName2" -> tilePosition)
    }
  }

  it can "remove a pawn from a tile" in {
    val board = newSimpleBoard
    board << ("pawnName" -> tilePosition) <# tilePosition
    board(tilePosition) should be (None)
  }

  it should "not allow removing from an empty tile" in {
    an [IllegalStateException] should be thrownBy {
      val board = newSimpleBoard
      board <# tilePosition
    }
  }

  it should "be able to provide a board map" in {
    val board = newSimpleBoard
    val pawnValue = "pawnName"
    board << (pawnValue -> tilePosition)
    board.boardMap should contain (tilePosition -> pawnValue)
  }
}
