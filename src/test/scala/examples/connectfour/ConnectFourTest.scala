package examples.connectfour

import org.scalatest.{FlatSpec, Matchers}
import examples.connectfour.ConnectFour._
import sbags.core.extension.Results.Winner
import sbags.core.extension._

class ConnectFourTest extends FlatSpec with Matchers {

  private val higherY = ConnectFour.height - 1

  behavior of "A connect four game"

  it should "start with an empty board" in {
    ConnectFour.newGame.currentState.board.boardMap shouldBe empty
  }

  it should "let the first player place a Red pawn" in {
    val game = ConnectFour.newGame
    game executeMove Put(0)
    game.currentState.board(0, higherY) should be (Some(Red))
  }

  it should "place a Blue pawn after a Red one has been placed" in {
    val game = ConnectFour.newGame
    game executeMove Put(0)
    game executeMove Put(1)
    game.currentState.board(1, higherY) should be (Some(Blue))
  }

  it should "correctly alternate Reds and Blues" in {
    val game = ConnectFour.newGame
    game executeMove Put(0)
    game executeMove Put(0)
    game executeMove Put(1)
    game.currentState.board(1, higherY) should be (Some(Red))
  }

  it should "place pawn in the first empty space" in {
    val game = ConnectFour.newGame
    game executeMove Put(0)
    game executeMove Put(0)
    game.currentState.board(0, higherY - 1) should be (Some(Blue))
  }

  it should "not execute a move when placing a pawn on an occupied column" in {
    val game = ConnectFour.newGame
    (0 until ConnectFour.height).foreach(_ => game executeMove Put(0))
    an [IllegalStateException] should be thrownBy {
      game executeMove Put(0)
    }
  }

  it should "know when Red wins" in {
    val game = ConnectFour.newGame
    if (ConnectFour.height >= ConnectFour.connectedToWin) { // else test should throw wrong exception
      (0 until ConnectFour.connectedToWin - 1).foreach(_ => {
        game executeMove Put(0)
        game executeMove Put(1)
      })
      game executeMove Put(0)
      game.currentState.gameResult should be(Some(Winner(Red)))
    }
  }

  it should "know when Blue wins" in {
    val game = ConnectFour.newGame
    if (ConnectFour.height > ConnectFour.connectedToWin) { // else test should throw wrong exception
      game executeMove Put(0)
      (0 until ConnectFour.connectedToWin - 1).foreach(_ => {
        game executeMove Put(0)
        game executeMove Put(1)
      })
      game executeMove Put(0)
      game.currentState.gameResult should be(Some(Winner(Blue)))
    }
  }

  it should "recognize a vertical winning condition" in {
    val game = ConnectFour.newGame
    if (ConnectFour.height >= ConnectFour.connectedToWin) { // else test should throw wrong exception
      (0 until ConnectFour.connectedToWin - 1).foreach(_ => {
        game executeMove Put(0)
        game executeMove Put(1)
      })
      game executeMove Put(0)
      game.currentState.gameResult should be(Some(Winner(Red)))
    }
  }

  it should "recognize an horizontal winning condition" in {
    val game = ConnectFour.newGame
    if (ConnectFour.width >= ConnectFour.connectedToWin) { // else test should throw wrong exception
      (1 until ConnectFour.connectedToWin).foreach(i => {
        game executeMove Put(i)
        game executeMove Put(i)
      })
      game executeMove Put(ConnectFour.connectedToWin)
      game.currentState.gameResult should be(Some(Winner(Red)))
    }
  }

  it should "recognize a descending diagonal winning condition" in {
    val game = ConnectFour.newGame
    if (ConnectFour.width >= ConnectFour.connectedToWin || ConnectFour.height >= ConnectFour.connectedToWin) { // else test should throw wrong exception
      (0 until ConnectFour.connectedToWin).foreach(_ => {
        (0 until ConnectFour.width).foreach(i => {
          game executeMove Put(i)
        })
      })
      game executeMove Put(0)
      game.currentState.gameResult should be(Some(Winner(Blue)))
    }
  }

  it should "recognize an ascending diagonal winning condition" in {
    val game = ConnectFour.newGame
    if (ConnectFour.width >= ConnectFour.connectedToWin || ConnectFour.height >= ConnectFour.connectedToWin) { // else test should throw wrong exception
      (0 until ConnectFour.connectedToWin).foreach(_ => {
        (ConnectFour.width -1  to 0 by -1).foreach(i => {
          game executeMove Put(i)
        })
      })
      game executeMove Put(ConnectFour.width - 1)
      game.currentState.gameResult should be(Some(Winner(Blue)))
    }
  }

}
