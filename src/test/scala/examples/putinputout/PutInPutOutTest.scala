package examples.putinputout

import org.scalatest.{FlatSpec, Matchers}

class PutInPutOutTest extends FlatSpec with Matchers {

  behavior of "a PutInPutOut game"

  it can "be created" in {
    new PutInPutOut().newGame shouldBe a [PutInPutOutState]
  }

  it should "start with an empty tile" in {
    val game = new PutInPutOut().newGame
    game.boardState(TheTile) should be (None)
  }

  it should "be able to put in the pawn in the tile, if the tile is empty" in {
    val game = new PutInPutOut().newGame
    PutIn execute game
    game.boardState(TheTile) should be (Some(ThePawn))
  }

  it should "be possible to put out the pawn from the tile, if the pawn is present" in {
    val game = new PutInPutOut().newGame
    PutIn execute game
    PutOut execute game
    game.boardState(TheTile) should be (None)
  }

  it should "throw IllegalStateException if two pawn are put in the same tile" in {
    assertThrows[IllegalStateException] {
      val game = new PutInPutOut().newGame
      PutIn execute game
      PutIn execute game
    }
  }

  it should "throw IllegalArgumentException if try to put out pawn from empty tile" in {
    assertThrows[IllegalArgumentException] {
      val game = new PutInPutOut().newGame
      PutOut execute game
    }
  }
}
