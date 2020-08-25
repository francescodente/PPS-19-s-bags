package examples.othello

import examples.othello.Othello._
import org.scalatest.{FlatSpec, Matchers}
import sbags.model.core.Board
import sbags.model.extension.Results.Winner
import sbags.model.extension._

class OthelloTest extends FlatSpec with Matchers {
  behavior of "An Othello game"

  it should "start with the initial board configuration" in {
    newGame.currentState.boardState should be (initialBoard)
  }

  it should "start with the black player first" in {
    newGame.currentState.currentPlayer should be (Black)
  }

  it should "end when the board is full" in {
    val board = OthelloBoard.tiles.foldLeft(Board(OthelloBoard))((b, t) => b.place(Black, t))
    OthelloState(board, Black).gameResult shouldNot be (empty)
  }

  it should "have the player with the highest number of pawns on the board as the winner" in {
    val board = OthelloBoard.tiles.foldLeft(Board(OthelloBoard))((b, t) => b.place(Black, t))
    OthelloState(board, Black).gameResult should be (Some(Winner(Black)))
  }
}
