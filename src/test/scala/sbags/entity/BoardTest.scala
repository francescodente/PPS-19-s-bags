package sbags.entity

import org.scalatest.{FlatSpec, Matchers}

class BoardTest extends FlatSpec with Matchers {

  val tilePosition: Int = 0
  def newSimpleBoard: BasicBoard {
    type Tile = Int
    type Pawn = String
  } = new BasicBoard {
    type Tile = Int
    type Pawn = String
    override def tiles: Seq[Int] = List(tilePosition)
  }

  behavior of "A board"

  it should "not have any pawn in an initialize tile" in {
    val board = newSimpleBoard
    board(tilePosition) should be (None)
  }

  it should "be able to add a pawn in a tile" in {
    val board = newSimpleBoard
    val pawnName: String = "pawnName"
    board << (pawnName -> tilePosition)
    board(tilePosition) should be (Some(pawnName))
  }

  it should "not allow placing a pawn on a non-empty tile" in {
    assertThrows[IllegalStateException] {
      val board = newSimpleBoard
      board << ("pawnName1" -> tilePosition)
      board << ("pawnName2" -> tilePosition)
    }
  }

  it should "be able to remove a pawn from a tile" in {
    val board = newSimpleBoard
    board << ("pawnName" -> tilePosition) <# tilePosition
    board(tilePosition) should be (None)
  }

  it should "be able to provide a board map" in {
    val board = newSimpleBoard
    val pawnValue = "pawnName"
    board << (pawnValue -> tilePosition)
    board.boardMap should contain (tilePosition -> pawnValue)
  }
}
