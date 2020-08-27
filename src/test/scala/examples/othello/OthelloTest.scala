package examples.othello

import examples.othello.Othello._
import org.scalatest.{FlatSpec, Matchers}
import sbags.model.core.{Board, Coordinate}
import sbags.model.extension.Results.{Draw, Winner}
import sbags.model.extension._

class OthelloTest extends FlatSpec with Matchers {
  private def fillBoard(pawnSupplier: Coordinate => OthelloPawn): Board[BoardStructure] =
    OthelloBoard.tiles.foldLeft(Board(OthelloBoard))((b, t) => b.place(pawnSupplier(t), t))

  private val withCheckerBoardPattern: Coordinate => OthelloPawn = {
    case t if (t.x + t.y) % 2 == 0 => White
    case _ => Black
  }

  private val withOneWhiteLine: Coordinate => OthelloPawn = {
    case t if t.x == 0 => White
    case _ => Black
  }

  private val withOnly: OthelloPawn => Coordinate => OthelloPawn = p => _ => p

  behavior of "An Othello game"

  it should "start with the initial board configuration" in {
    newGame.currentState.boardState should be(initialBoard)
  }

  it should "start with the black player first" in {
    newGame.currentState.currentPlayer should be(Black)
  }

  it should "end when the board is full" in {
    val board = fillBoard(withOnly(Black))
    OthelloState(board, Black).gameResult shouldNot be(empty)
  }

  it should "end when no moves are available for both players" in {
    val board = fillBoard(withOnly(Black)).clear(0, 0)
    OthelloState(board, White).gameResult shouldNot be(empty)
  }

  it should "end with the player with the highest number of pawns on the board as the winner" in {
    val board = fillBoard(withOneWhiteLine)
    OthelloState(board, Black).gameResult should be(Some(Winner(Black)))
  }

  it should "end in a draw when pawns are in equal number" in {
    val board = fillBoard(withCheckerBoardPattern)
    OthelloState(board, Black).gameResult should be(Some(Draw))
  }
}
