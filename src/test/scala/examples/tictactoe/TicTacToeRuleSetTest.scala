package examples.tictactoe

import org.scalatest.{FlatSpec, Matchers}

class TicTacToeRuleSetTest extends FlatSpec with Matchers {
  val allValidMoves: Seq[TicTacToeMove] = for (x <- 0 until 3; y <- 0 until 3) yield Put((x, y))

  behavior of "A tic-tac-toe ruleSet"

  it should "generate all the available moves" in {
    implicit val state: TicTacToeState = TicTacToe.newGame
    state.ruleSet.availableMoves should contain theSameElementsAs allValidMoves
  }

  it should "remove a move after using it" in {
    implicit val state: TicTacToeState = TicTacToe.newGame
    val move: TicTacToeMove = state.ruleSet.availableMoves.head
    state executeMove move
    state.ruleSet.availableMoves should contain theSameElementsAs (allValidMoves filter (_ != move))
  }

  it should "accept all the valid moves" in {
    implicit val state: TicTacToeState = TicTacToe.newGame
    allValidMoves map (state.ruleSet.isValid(_)) reduce (_ && _) should be (true)
  }
}
