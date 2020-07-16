package sbags.core

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class GameStateTest extends FlatSpec with MockFactory with Matchers {
  object TestBoard extends BoardStructure {
    override type Tile = Int
    override type Pawn = String

    override def tiles: Seq[Int] = List()
  }

  behavior of "A new game state"

  it should "have the board it is passed" in {
    val board = Board(TestBoard)
    val gameState: BasicGameState[TestBoard.type] = new BasicGameState[TestBoard.type](board)
    gameState.boardState should be (board)
  }
}
