package sbags.core

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.core.Mocks.TestState

class ModifiablePlayersTest extends FlatSpec with Matchers with MockFactory {
  class TestBoardState(override val initialPlayers: Set[Int]) extends TestState with ModifiablePlayers[Int]

  private val playersSet = Set(1, 2, 3, 4)

  behavior of "A game state with a modifiable set of players"

  it should "contain the initial set of players" in {
    val gameState = new TestBoardState(playersSet)
    gameState.players should contain theSameElementsAs playersSet
  }

  it should "be able to add a new player" in {
    val gameState = new TestBoardState(playersSet)
    val toBeAdded = 5
    gameState addPlayer toBeAdded
    gameState.players should contain theSameElementsAs (playersSet + toBeAdded)
  }

  it should "be able to remove an existing player" in {
    val gameState = new TestBoardState(playersSet)
    val toBeRemoved = 4
    gameState removePlayer toBeRemoved
    gameState.players should contain theSameElementsAs (playersSet - toBeRemoved)
  }

  it should "not modify the initial set of players" in {
    val gameState = new TestBoardState(playersSet)
    val toBeAdded = 5
    val toBeRemoved = 4
    gameState addPlayer toBeAdded
    gameState removePlayer toBeRemoved
    gameState.initialPlayers should contain theSameElementsAs playersSet
  }
}
