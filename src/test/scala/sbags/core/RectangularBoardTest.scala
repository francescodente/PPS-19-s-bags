package sbags.core

import org.scalatest.{FlatSpec, Matchers}

class RectangularBoardTest extends FlatSpec with Matchers  {
  private val width: Int = 3
  private val height: Int = 5
  private val validPosition: (Int, Int) = (0, 0)
  private val invalidPosition: (Int, Int) = (4, 6)
  private val pawnName = "pawn"

  private def createBoard(width: Int, height: Int) = new RectangularBoard(width, height) {
    type Pawn = String
  }

  behavior of "A rectangular board"

  it can "be created" in {
    createBoard(width, height)
  }

  it should "not allow an invalid set operation" in {
    val board = Board(createBoard(width, height))
    board place (pawnName, validPosition)
    an [IllegalArgumentException] should be thrownBy {
      board place (pawnName, invalidPosition)
    }
  }

  it should "not allow an invalid remove operation" in {
    val board = Board(createBoard(width, height))
    an [IllegalArgumentException] should be thrownBy {
      board clear invalidPosition
    }
  }
}
