package examples.othello

import org.scalatest.{FlatSpec, Matchers}
import examples.othello.Othello._
import sbags.core.Board

class OthelloRuleSetTest extends FlatSpec with Matchers {
  private def newEmptyBoard: Board[BoardStructure] = Board(OthelloBoard)

  behavior of "Othello move generation"

  it should "generate capture moves only" in {
    val board = newEmptyBoard
      .place(Black, (1, 1))
      .place(White, (1, 2))
      .place(White, (2, 1))
      .place(White, (3, 1))
      .place(White, (2, 2))
    val state = OthelloState(board, Black)
    OthelloRuleSet.availableMoves(state) should contain theSameElementsAs Seq(
      Put(1, 3), Put(3, 3), Put(4, 1)
    )
  }
}
