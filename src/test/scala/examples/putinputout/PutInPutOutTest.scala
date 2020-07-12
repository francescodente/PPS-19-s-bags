package examples.putinputout

import org.scalatest.{FlatSpec, Matchers}

class PutInPutOutTest extends FlatSpec with Matchers {
  behavior of "a PutInPutOut game"

  it can "be created" in {
    PutInPutOut.newGame shouldBe a [PutInPutOutState]
  }

  it should "start with an empty tile" in {
    val gameState = PutInPutOut.newGame
    gameState.boardState(TheTile) should be (None)
  }

  it should "be able to put in the pawn in the tile, if the tile is empty" in {
    val gameState = PutInPutOut.newGame
    gameState executeMove PutIn
    gameState.boardState(TheTile) should be (Some(ThePawn))
  }

  it should "be possible to put out the pawn from the tile, if the pawn is present" in {
    val gameState = PutInPutOut.newGame
    gameState executeMove PutIn
    gameState executeMove PutOut
    gameState.boardState(TheTile) should be (None)
  }

  it should "not execute move (return false) if two pawn are put in the same tile" in {
    val gameState = PutInPutOut.newGame
    gameState executeMove PutIn
    an [IllegalStateException] should be thrownBy {
      gameState executeMove PutIn
    }
  }

  it should "not execute move (return false) if try to put out pawn from empty tile" in {
    val gameState = PutInPutOut.newGame
    an [IllegalStateException] should be thrownBy {
      gameState executeMove PutOut
    }
  }
}
