package examples.connectfour

import examples.connectfour.ConnectFour._
import org.scalatest.{FlatSpec, Matchers}
import sbags.model.core.InvalidMove
import sbags.model.extension.Results.{Draw, Winner}
import sbags.model.extension._

class ConnectFourTest extends FlatSpec with Matchers {

  private val higherY = ConnectFour.height - 1

  behavior of "A connect four game"

  it should "start with an empty board" in {
    ConnectFour.newGame.currentState.board.boardMap should contain theSameElementsAs Map.empty
  }

  it should "let the first player place a Red pawn" in {
    val game = ConnectFour.newGame
    game executeMove Put(0)
    game.currentState.board(0, higherY) should be(Some(Red))
  }

  it should "place a Blue pawn after a Red one has been placed" in {
    val game = ConnectFour.newGame
    game executeMove Put(0)
    game executeMove Put(1)
    game.currentState.board(1, higherY) should be(Some(Blue))
  }

  it should "correctly alternate Reds and Blues" in {
    val game = ConnectFour.newGame
    game executeMove Put(0)
    game executeMove Put(0)
    game executeMove Put(1)
    game.currentState.board(1, higherY) should be(Some(Red))
  }

  it should "place pawn in the first empty space" in {
    val game = ConnectFour.newGame
    game executeMove Put(0)
    game executeMove Put(0)
    game.currentState.board(0, higherY - 1) should be(Some(Blue))
  }

  it should "not execute a move when placing a pawn on an occupied column" in {
    val game = ConnectFour.newGame
    for (_ <- 0 until ConnectFour.height) game executeMove Put(0)

    game executeMove Put(0) should be(Left(InvalidMove))
  }

  it should "know when Red wins" in {
    val game = ConnectFour.newGame
    if (ConnectFour.height >= ConnectFour.connectedToWin) {
      for (_ <- 0 until ConnectFour.connectedToWin - 1) {
        game executeMove Put(0)
        game executeMove Put(1)
      }
      game executeMove Put(0)
      game.currentState.gameResult should be(Some(Winner(Red)))
    }
  }

  it should "know when Blue wins" in {
    val game = ConnectFour.newGame
    if (ConnectFour.height > ConnectFour.connectedToWin) {
      game executeMove Put(0)
      for (_ <- 0 until ConnectFour.connectedToWin - 1) {
        game executeMove Put(0)
        game executeMove Put(1)
      }
      game executeMove Put(0)
      game.currentState.gameResult should be(Some(Winner(Blue)))
    }
  }

  it should "recognize a vertical winning condition" in {
    val game = ConnectFour.newGame
    if (ConnectFour.height >= ConnectFour.connectedToWin) {
      for (_ <- 0 until ConnectFour.connectedToWin - 1) {
        game executeMove Put(0)
        game executeMove Put(1)
      }
      game executeMove Put(0)
      game.currentState.gameResult should be(Some(Winner(Red)))
    }
  }

  it should "recognize an horizontal winning condition" in {
    val game = ConnectFour.newGame
    if (ConnectFour.width >= ConnectFour.connectedToWin) {
      for (i <- 1 until ConnectFour.connectedToWin) {
        game executeMove Put(i)
        game executeMove Put(i)
      }
      game executeMove Put(ConnectFour.connectedToWin)
      game.currentState.gameResult should be(Some(Winner(Red)))
    }
  }

  it should "recognize a descending diagonal winning condition" in {
    val game = ConnectFour.newGame
    if (ConnectFour.width >= ConnectFour.connectedToWin || ConnectFour.height >= ConnectFour.connectedToWin) {
      for {
        _ <- 0 until ConnectFour.connectedToWin
        i <- 0 until ConnectFour.width
      } game executeMove Put(i)
      game executeMove Put(0)
      game.currentState.gameResult should be(Some(Winner(Blue)))
    }
  }

  it should "recognize an ascending diagonal winning condition" in {
    val game = ConnectFour.newGame
    if (ConnectFour.width >= ConnectFour.connectedToWin || ConnectFour.height >= ConnectFour.connectedToWin) {
      for {
        _ <- 0 until ConnectFour.connectedToWin
        i <- ConnectFour.width -1 to 0 by -1
      } game executeMove Put(i)
      game executeMove Put(ConnectFour.width - 1)
      game.currentState.gameResult should be(Some(Winner(Blue)))
    }
  }

  it should "have a draws as result when the board is full and no one won" in {
    val game = ConnectFour.newGame
    var acc = 0
    for (i <- 0 until ConnectFour.width - 1 by ConnectFour.connectedToWin - 1) {
      for {
        j <- i until ConnectFour.connectedToWin - 1 + i
        _ <- 0 until ConnectFour.height
      } {
        game executeMove Put(j)
      }
      game executeMove Put(ConnectFour.width - 1)
      acc = acc + 1
    }
    (acc until ConnectFour.height).foreach(_ => game executeMove Put(ConnectFour.width - 1))
    game.currentState.gameResult should be(Some(Draw))
  }
}
