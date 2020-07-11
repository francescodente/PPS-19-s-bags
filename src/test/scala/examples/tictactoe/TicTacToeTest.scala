package examples.tictactoe

import org.scalatest.{FlatSpec, Matchers}

class TicTacToeTest extends FlatSpec with Matchers {
  private val upperLeft = (0, 0)
  private val upperCenter = (0, 1)
  private val upperRight = (0, 2)

  private val centerLeft = (1, 0)
  private val center = (1, 1)
  private val centerRight = (1, 2)

  private val bottomLeft = (2, 0)
  private val bottomCenter = (2, 1)
  private val bottomRight = (2, 2)

  behavior of "A tic-tac-toe game"

  it should "start with an empty board" in {
    TicTacToe.newGame.boardState.boardMap shouldBe empty
  }

  it should "let the first player place an X" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeft)
    gameState.boardState(upperLeft) should be (Some(X))
  }

  it should "place an O after an X has been placed" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeft)
    gameState executeMove Put(upperCenter)
    gameState.boardState(upperCenter) should be (Some(O))
  }

  it should "correctly alternate Xs and Os" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeft)
    gameState executeMove Put(upperCenter)
    gameState executeMove Put(upperRight)
    gameState.boardState(upperRight) should be (Some(X))
  }

  it should "not execute a move when placing a pawn on an occupied tile" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeft)
    assertThrows[IllegalStateException] {
      gameState executeMove Put(upperLeft)
    }
  }

  it should "know when X wins" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeft)
    gameState executeMove Put(bottomRight)
    gameState executeMove Put(upperCenter)
    gameState executeMove Put(bottomCenter)
    gameState executeMove Put(upperRight)
    gameState.gameResult should be (Some(Winner(X)))
  }

  it should "know when O wins" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(bottomRight)
    gameState executeMove Put(upperRight)
    gameState executeMove Put(bottomCenter)
    gameState executeMove Put(upperCenter)
    gameState executeMove Put(center)
    gameState executeMove Put(upperLeft)
    gameState.gameResult should be (Some(Winner(O)))
  }

  it should "recognize a vertical winning condition" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeft)
    gameState executeMove Put(upperCenter)
    gameState executeMove Put(centerLeft)
    gameState executeMove Put(center)
    gameState executeMove Put(bottomLeft)
    gameState.gameResult should be (Some(Winner(X)))
  }

  it should "recognize an horizontal winning condition" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeft)
    gameState executeMove Put(centerLeft)
    gameState executeMove Put(upperCenter)
    gameState executeMove Put(bottomLeft)
    gameState executeMove Put(upperRight)
    gameState.gameResult should be (Some(Winner(X)))
  }

  it should "recognize a left-right diagonal winning condition" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperLeft)
    gameState executeMove Put(centerLeft)
    gameState executeMove Put(center)
    gameState executeMove Put(bottomLeft)
    gameState executeMove Put(bottomRight)
    gameState.gameResult should be (Some(Winner(X)))
  }

  it should "recognize a right-left diagonal winning condition" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperRight)
    gameState executeMove Put(centerLeft)
    gameState executeMove Put(center)
    gameState executeMove Put(bottomRight)
    gameState executeMove Put(bottomLeft)
    gameState.gameResult should be (Some(Winner(X)))
  }

  it should "have a draws as result when the board is full and no one won" in {
    val gameState = TicTacToe.newGame
    gameState executeMove Put(upperCenter)
    gameState executeMove Put(center)
    gameState executeMove Put(upperRight)
    gameState executeMove Put(upperLeft)
    gameState executeMove Put(bottomRight)
    gameState executeMove Put(centerRight)
    gameState executeMove Put(centerLeft)
    gameState executeMove Put(bottomLeft)
    gameState executeMove Put(bottomCenter)
    gameState.gameResult should be (Some(Draw))
  }
}
