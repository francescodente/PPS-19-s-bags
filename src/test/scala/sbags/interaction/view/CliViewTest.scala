package sbags.interaction.view

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers, PrivateMethodTester}
import sbags.core.{BasicRectangularBoard, RectangularBoard}

class CliViewTest extends FlatSpec with MockFactory with Matchers with PrivateMethodTester {
  behavior of "A CliView"

  /*trait RectangularBoardTest extends RectangularBoard {
    override val width: Int = 5
    override val height: Int = 5
  } //check if it is possible, i still could find a smart way
  mock[RectangularBoardTest]
   */


  it should "be able to display a 1 row board" in {
    val BoardAsString = "1 _ _ _ _ _ \n  1 2 3 4 5 \n"
    val oneRowBoard = new BasicRectangularBoard(5,1)
    val buildBoard = PrivateMethod[String]('buildBoard)
    new CliView[RectangularBoard](i => i+1+"", j=>j+1+"") invokePrivate buildBoard(oneRowBoard) should be(BoardAsString)
  }

  it should "be able to display a 1 column board" in {
    val BoardAsString = "1 _ \n2 _ \n3 _ \n  1 \n"
    val oneColumnBoard = new BasicRectangularBoard(1,3)
    val buildBoard = PrivateMethod[String]('buildBoard)
    new CliView[RectangularBoard](i => i+1+"", j=>j+1+"") invokePrivate buildBoard(oneColumnBoard) should be(BoardAsString)
  }

  it should "be able to display a 2x3 board" in {
    val BoardAsString = "1 _ _ \n2 _ _ \n3 _ _ \n  1 2 \n"
    val rectangularBoard = new BasicRectangularBoard(2,3)
    val buildBoard = PrivateMethod[String]('buildBoard)
    new CliView[RectangularBoard](i => i+1+"", j=>j+1+"") invokePrivate buildBoard(rectangularBoard) should be(BoardAsString)
  }

}
