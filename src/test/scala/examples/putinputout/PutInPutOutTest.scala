package examples.putinputout

import org.scalatest.{FlatSpec, Matchers}

class PutInPutOutTest extends FlatSpec with Matchers {

  behavior of "a PutInPutOut game"

  it can "be created" in {
    PutInPutOut.newGame shouldBe a [PutInPutOutState]
  }

  it should "start with an empty tile" in {
    val game = PutInPutOut.newGame
    game.boardState(TheTile) should be (None)
  }

  it should "be able to put in the pawn in the tile, if the tile is empty" in {
    implicit val game: PutInPutOutState = PutInPutOut.newGame
    PutInPutOut executeMove PutIn
    game.boardState(TheTile) should be (Some(ThePawn))
  }

  it should "be possible to put out the pawn from the tile, if the pawn is present" in {
    implicit val game: PutInPutOutState = PutInPutOut.newGame
    PutInPutOut executeMove PutIn
    PutInPutOut executeMove PutOut
    game.boardState(TheTile) should be (None)
  }

  it should "throw IllegalStateException if two pawn are put in the same tile" in {
    assertThrows[IllegalStateException] {
      implicit val game: PutInPutOutState = PutInPutOut.newGame
      PutInPutOut executeMove PutIn
      PutInPutOut executeMove PutIn
    }
  }

  it should "throw IllegalStateException if try to put out pawn from empty tile" in {
    assertThrows[IllegalStateException] {
      implicit val game: PutInPutOutState = PutInPutOut.newGame
      PutInPutOut executeMove PutOut
    }
  }
}
