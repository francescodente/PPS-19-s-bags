package sbags.core

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class RectangularBoardExtensionsTest extends FlatSpec with Matchers with MockFactory {

  behavior of "A rectangular board with extensions"

  var descendingDiagonals: Stream[Stream[Coordinate]] = Stream(
    Stream((0,2)),
    Stream((0,1),(1,2)),
    Stream((0,0),(1,1),(2,2)),
    Stream((1,0),(2,1)),
    Stream((2,0)))

  it should "know all of its descending diagonals" in {
    val board = new RectangularBoard(3, 3)

    board.descendingDiagonals should contain theSameElementsAs descendingDiagonals
  }

}
