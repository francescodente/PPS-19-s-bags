package examples.othello

import org.scalatest.{FlatSpec, Matchers}
import sbags.model.extension._
import Othello._

class OthelloTest extends FlatSpec with Matchers {
  behavior of "An Othello game"

  it should "start with the initial board configuration" in {
    newGame.currentState.boardState should be (initialBoard)
  }

  it should "start with the black player first" in {
    newGame.currentState.currentPlayer should be (Black)
  }
}
