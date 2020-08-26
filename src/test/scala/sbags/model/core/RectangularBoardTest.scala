package sbags.model.core

import org.scalatest.{FlatSpec, Matchers}

class RectangularBoardTest extends FlatSpec with Matchers {
  private val width: Int = 3
  private val height: Int = 5
  private val validPosition: (Int, Int) = (0, 0)
  private val invalidPosition: (Int, Int) = (4, 6)
  private val pawnName = "pawn"
  private val fullCoordinates = for (x <- 0 until width; y <- 0 until height) yield Coordinate(x, y)

  private def createBoard(width: Int, height: Int) = new RectangularStructure(width, height) {
    type Pawn = String
  }

  behavior of "A rectangular board 3x5"

  it can "be created" in {
    createBoard(width, height)
  }

  it should "not allow an invalid set operation" in {
    val board = Board(createBoard(width, height))
    board place(pawnName, validPosition)
    an[IllegalArgumentException] should be thrownBy {
      board place(pawnName, invalidPosition)
    }
  }

  it should "not allow an invalid remove operation" in {
    val board = Board(createBoard(width, height))
    an[IllegalArgumentException] should be thrownBy {
      board clear invalidPosition
    }
  }

  it should "have a row of 3 elements" in {
    val board = createBoard(width, height)
    board.row(0).size should be(3)
  }

  it should "have a col of 5 elements" in {
    val board = createBoard(width, height)
    board.col(0).size should be(5)
  }

  it should "have 5 rows" in {
    val board = createBoard(width, height)
    board.rows.size should be(5)
  }

  it should "have 3 cols" in {
    val board = createBoard(width, height)
    board.cols.size should be(3)
  }

  it should "be able to return every coordinate grouped by cols" in {
    val board = createBoard(width, height)
    board.cols.flatten should contain theSameElementsAs fullCoordinates
  }

  it should "be able to return every coordinate grouped by rows" in {
    val board = createBoard(width, height)
    board.rows.flatten should contain theSameElementsAs fullCoordinates
  }
}
