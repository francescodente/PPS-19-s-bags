package examples.tictactoe

import org.scalatest.{FlatSpec, Matchers}
import sbags.core.extension.Results.{Draw, Winner}
import sbags.core.extension._
import examples.tictactoe.TicTacToe._

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
    TicTacToe.newGame.currentState.board.boardMap shouldBe empty
  }

  it should "let the first player place an X" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperLeft)
    game.currentState.board(upperLeft) should be (Some(X))
  }

  it should "place an O after an X has been placed" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperLeft)
    game executeMove Put(upperCenter)
    game.currentState.board(upperCenter) should be (Some(O))
  }

  it should "correctly alternate Xs and Os" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperLeft)
    game executeMove Put(upperCenter)
    game executeMove Put(upperRight)
    game.currentState.board(upperRight) should be (Some(X))
  }

  it should "not execute a move when placing a pawn on an occupied tile" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperLeft)
    an [IllegalStateException] should be thrownBy {
      game executeMove Put(upperLeft)
    }
  }

  it should "know when X wins" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperLeft)
    game executeMove Put(bottomRight)
    game executeMove Put(upperCenter)
    game executeMove Put(bottomCenter)
    game executeMove Put(upperRight)
    game.currentState.gameResult should be (Some(Winner(X)))
  }

  it should "know when O wins" in {
    val game = TicTacToe.newGame
    game executeMove Put(bottomRight)
    game executeMove Put(upperRight)
    game executeMove Put(bottomCenter)
    game executeMove Put(upperCenter)
    game executeMove Put(center)
    game executeMove Put(upperLeft)
    game.currentState.gameResult should be (Some(Winner(O)))
  }

  it should "recognize a vertical winning condition" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperLeft)
    game executeMove Put(upperCenter)
    game executeMove Put(centerLeft)
    game executeMove Put(center)
    game executeMove Put(bottomLeft)
    game.currentState.gameResult should be (Some(Winner(X)))
  }

  it should "recognize an horizontal winning condition" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperLeft)
    game executeMove Put(centerLeft)
    game executeMove Put(upperCenter)
    game executeMove Put(bottomLeft)
    game executeMove Put(upperRight)
    game.currentState.gameResult should be (Some(Winner(X)))
  }

  it should "recognize a left-right diagonal winning condition" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperLeft)
    game executeMove Put(centerLeft)
    game executeMove Put(center)
    game executeMove Put(bottomLeft)
    game executeMove Put(bottomRight)
    game.currentState.gameResult should be (Some(Winner(X)))
  }

  it should "recognize a right-left diagonal winning condition" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperRight)
    game executeMove Put(centerLeft)
    game executeMove Put(center)
    game executeMove Put(bottomRight)
    game executeMove Put(bottomLeft)
    game.currentState.gameResult should be (Some(Winner(X)))
  }

  it should "have a draws as result when the board is full and no one won" in {
    val game = TicTacToe.newGame
    game executeMove Put(upperCenter)
    game executeMove Put(center)
    game executeMove Put(upperRight)
    game executeMove Put(upperLeft)
    game executeMove Put(bottomRight)
    game executeMove Put(centerRight)
    game executeMove Put(centerLeft)
    game executeMove Put(bottomLeft)
    game executeMove Put(bottomCenter)
    game.currentState.gameResult should be (Some(Draw))
  }
}
