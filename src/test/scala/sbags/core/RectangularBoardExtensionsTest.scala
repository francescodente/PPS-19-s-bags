package sbags.core

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class RectangularBoardExtensionsTest extends FlatSpec with Matchers with MockFactory {

  behavior of "A rectangular board with extensions"

  val descendingDiagonals: Stream[Stream[Coordinate]] = Stream(
    Stream((2,0)),
    Stream((1,0),(2,1)),
    Stream((0,0),(1,1),(2,2)),
    Stream((0,1),(1,2)),
    Stream((0,2)))

  val ascendingDiagonals: Stream[Stream[Coordinate]] = Stream(
    Stream((0,0)),
    Stream((0,1),(1,0)),
    Stream((0,2),(1,1),(2,0)),
    Stream((1,2),(2,1)),
    Stream((2,2))
  )

  val allDiagonals: Stream[Stream[Coordinate]] = Stream(
    Stream((1,0)),
    Stream((0,0),(1,1)),
    Stream((0,1),(1,2)),
    Stream((0,2),(1,3)),
    Stream((0,3)),
    Stream((0,0)),
    Stream((0,1),(1,0)),
    Stream((0,2),(1,1)),
    Stream((0,3),(1,2)),
    Stream((1,3))
  )

  val allDiagonals2: Stream[Stream[Coordinate]] = Stream(
    Stream((0,0)),
    Stream((0,1),(1,0)),
    Stream((1,1),(2,0)),
    Stream((2,1),(3,0)),
    Stream((3,1)),
    Stream((0,1)),
    Stream((0,0),(1,1)),
    Stream((1,0),(2,1)),
    Stream((2,0),(3,1)),
    Stream((3,0))
  )

  it should "know all of its descending diagonals" in {
    val board = new RectangularBoard(3, 3)

    board.descendingDiagonals should contain theSameElementsAs descendingDiagonals
  }

  it should "know all of its ascending diagonals" in {
    val board = new RectangularBoard(3, 3)

    board.ascendingDiagonals should contain theSameElementsAs ascendingDiagonals
  }

  it should "know all of its diagonals when its height is greater than its width" in {
    val board = new RectangularBoard(2,4)

    (board.ascendingDiagonals ++ board.descendingDiagonals) should contain theSameElementsAs allDiagonals
  }

  it should "know all of its diagonals when its width is greater than its height" in {
    val board = new RectangularBoard(4, 2)

    (board.ascendingDiagonals ++ board.descendingDiagonals) should contain theSameElementsAs allDiagonals2
  }

}
