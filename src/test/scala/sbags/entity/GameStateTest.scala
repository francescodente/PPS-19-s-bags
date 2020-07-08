package sbags.entity

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class GameStateTest extends FlatSpec with MockFactory with Matchers {

  private val board = new BasicBoard {
    override type Tile = Int
    override type Pawn = String
  }

  "A new game state" should "have the board it is passed" in {
    val gameState = new BasicGameState[BasicBoard](board){
      type Move = Any
      override def executeMove(move: Any): Unit = {}
    }
    gameState.boardState should be (board)
  }
}
