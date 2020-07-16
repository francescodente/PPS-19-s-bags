package examples.putinputout

import org.scalatest.{FlatSpec, Matchers}
import sbags.core.Game

class PutInPutOutTest extends FlatSpec with Matchers {
  behavior of "a PutInPutOut game"

  it can "be created" in {
    PutInPutOut.newGame shouldBe a [Game[_, _]]
  }

  it should "start with an empty tile" in {
    val game = PutInPutOut.newGame
    game.currentState.boardState(TheTile) should be (None)
  }

  it should "be able to put in the pawn in the tile, if the tile is empty" in {
    val game = PutInPutOut.newGame
    game executeMove PutIn
    game.currentState.boardState(TheTile) should be (Some(ThePawn))
  }

  it should "be possible to put out the pawn from the tile, if the pawn is present" in {
    val game = PutInPutOut.newGame
    game executeMove PutIn
    game executeMove PutOut
    game.currentState.boardState(TheTile) should be (None)
  }

  it should "not execute move (return false) if two pawn are put in the same tile" in {
    val game = PutInPutOut.newGame
    game executeMove PutIn
    an [IllegalStateException] should be thrownBy {
      game executeMove PutIn
    }
  }

  it should "not execute move (return false) if try to put out pawn from empty tile" in {
    val game = PutInPutOut.newGame
    an [IllegalStateException] should be thrownBy {
      game executeMove PutOut
    }
  }
}
