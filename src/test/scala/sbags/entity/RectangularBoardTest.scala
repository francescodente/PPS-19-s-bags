package sbags.entity

import org.scalatest.{FlatSpec, Matchers}

class RectangularBoardTest extends FlatSpec with Matchers  {

  def createBoard(width: Int, height: Int): BasicRectangularBoard {
    type Pawn = String
  } = new BasicRectangularBoard(width, height) {
    type Pawn = String
  }
  val width: Int = 3
  val height: Int = 5
  val validPosition: (Int, Int) = (0, 0)
  val invalidPosition: (Int, Int) = (4, 4)
  val pawnName = "pawn"

  behavior of "A rectangular board "

  it can "be created" in {
    createBoard(width, height)
  }

  it should "throw an IllegalStateException if an invalid set is done" in {
    val board = createBoard(width, height)
    board << (pawnName -> validPosition)
    assertThrows[IllegalStateException] {
      board << (pawnName -> invalidPosition)
    }
  }

  it should "throw an IllegalStateException if an invalid remove is done" in {
    val board = createBoard(width, height)
    assertThrows[IllegalStateException] {
      board <# invalidPosition
    }
  }

}
