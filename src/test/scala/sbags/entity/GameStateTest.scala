package sbags.entity

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class GameStateTest extends FlatSpec with MockFactory with Matchers {

  private val board: BasicBoard = new BasicBoard {
    override type Tile = Int
    override type Pawn = String

    /**
     *
     * @return the sequence containing all the valid tile for this board.
     */
    override def tiles: Seq[Int] = List()
  }

  behavior of "A new game state"

  it should "have the board it is passed" in {
    val gameState: BasicGameState[BasicBoard] = new BasicGameState[BasicBoard](board){
      type Move = Any
      override def executeMove(move: Any): Boolean = {true}

      override def ruleSet: RuleSet[Any, this.type] = ???
    }
    gameState.boardState should be (board)
  }
}
