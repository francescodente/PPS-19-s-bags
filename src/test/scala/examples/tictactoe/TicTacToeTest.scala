package examples.tictactoe

import org.scalatest.{FlatSpec, Matchers}

class TicTacToeTest extends FlatSpec with Matchers {
  private val upperLeftCorner = (0, 0)
  private val upperCenter = (0, 1)

  behavior of "A tic-tac-toe game"

  it should "start with an empty board" in {
    TicTacToe.newGame.boardState.boardMap shouldBe empty
  }

  it should "let the first player place an X" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeftCorner)
    gameState.boardState(upperLeftCorner) should be (Some(X))
  }

  it should "place an O after an X has been placed" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeftCorner)
    gameState executeMove Put(upperCenter)
    gameState.boardState(upperCenter) should be (Some(O))
  }

  it should "not execute a move when placing a pawn on an occupied tile" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeftCorner)
    gameState executeMove Put(upperLeftCorner) should be (false)
  }

  it should "know when someone wins" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(0,0)
    gameState executeMove Put(2,2)
    gameState executeMove Put(0,1)
    gameState executeMove Put(2,1)
    gameState executeMove Put(0,2)
    gameState.gameResult should be (Some(Winner(X)))
  }
}
