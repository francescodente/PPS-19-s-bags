package sbags.entity

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class GameStateTest extends FlatSpec with MockFactory with Matchers {

  private val board: BasicBoard = new BasicBoard {
    override type Tile = Int
    override type Pawn = String

    override def tiles: Seq[Int] = List()
  }

  "A new game state" should "have the board it is passed" in {
    val gameState: BasicGameState[BasicBoard] = new BasicGameState[BasicBoard](board) with Mocks.DefaultTestState
    gameState.boardState should be (board)
  }
}
