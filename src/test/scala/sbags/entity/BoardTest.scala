package sbags.entity

import org.scalatest.{FlatSpec, Matchers}

class BoardTest extends FlatSpec with Matchers {

  val tilePosition: Int = 0

  behavior of "A board"

  it should "return None from not initialize tile" in {
    val board = new BasicBoard {
      type Tile = Int
      type Pawn = String
    }
    board(tilePosition) should be (None)
  }

  it should "be able to add a pawn in a tile" in {
    val board = new BasicBoard {
      type Tile = Int
      type Pawn = String
    }
    val pawnName: String = "pawnName"
    board << (pawnName -> tilePosition)
    board(tilePosition) should be (Some(pawnName))
  }

  it should "throw an IllegalStateException when placing a pawn on a non-empty tile" in {
    assertThrows[IllegalStateException] {
      val board = new BasicBoard {
        type Tile = Int
        type Pawn = String
      }
      board << ("pawnName1" -> tilePosition)
      board << ("pawnName2" -> tilePosition)
    }
  }

  it should "be able to remove a pawn from a tile" in {
    val board = new BasicBoard {
      type Tile = Int
      type Pawn = String
    }
    board << ("pawnName" -> tilePosition) <# tilePosition
    board(tilePosition) should be (None)
  }

  it should "be possible to get a boardMap" in {
    val board = new BasicBoard {
      type Tile = Int
      type Pawn = String
    }
    val pawnValue = "pawnName"
    board << (pawnValue -> tilePosition)
    board.getBoardMap should contain (tilePosition -> pawnValue)
  }
}
