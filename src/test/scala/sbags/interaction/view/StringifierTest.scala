package sbags.interaction.view

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers, PrivateMethodTester}
import sbags.core.{Board, RectangularBoard}

class StringifierTest extends FlatSpec with MockFactory with Matchers with PrivateMethodTester {
  behavior of "A Stringifier"

  it should "be able to stringify an empty 1 row board" in {
    val BoardAsString = "1 _ _ _ _ _ \n  1 2 3 4 5 \n"
    val oneRowBoard = Board(new RectangularBoard(5,1))
    val computedBoard = new Stringifier[RectangularBoard](_+1+"", _+1+"") buildBoard oneRowBoard
    computedBoard should be(BoardAsString)
  }

  it should "be able to stringify an empty 1 column board" in {
    val BoardAsString = "1 _ \n2 _ \n3 _ \n  1 \n"
    val oneColumnBoard = Board(new RectangularBoard(1,3))
    val computedBoard = new Stringifier[RectangularBoard](_+1+"", _+1+"") buildBoard oneColumnBoard
    computedBoard should be(BoardAsString)
  }

  it should "be able to stringify an empty 2x3 board" in {
    val BoardAsString = "1 _ _ \n2 _ _ \n3 _ _ \n  1 2 \n"
    val rectangularBoard = Board(new RectangularBoard(2,3))
    val computedBoard = new Stringifier[RectangularBoard](_+1+"", _+1+"") buildBoard rectangularBoard
    computedBoard should be(BoardAsString)
  }

  it should "be able to stringify an empty 2x3 board using letters as column identifier" in {
    val letters = List("A","B","C")
    val BoardAsString = "A _ _ \nB _ _ \nC _ _ \n  1 2 \n"
    val rectangularBoard = Board(new RectangularBoard(2,3))
    val computedBoard = new Stringifier[RectangularBoard](_+1+"", letters(_)) buildBoard rectangularBoard
    computedBoard should be(BoardAsString)
  }

  it should "be able to stringify a non-empty 2x3 board" in {
    trait PawnTest
    object X extends PawnTest {
      override def toString: String = "X"
    }
    val BoardAsString = "1 X _ \n2 _ X \n3 X _ \n  1 2 \n"
    val rectangularBoard = Board(new RectangularBoard(2,3) {
      type Pawn = PawnTest
    }) place (X, (0,0)) place (X, (1,1)) place (X, (0,2))
    val computedBoard = new Stringifier[RectangularBoard {type Pawn = PawnTest}](_+1+"", _+1+"") buildBoard rectangularBoard
    computedBoard should be(BoardAsString)
  }
}
