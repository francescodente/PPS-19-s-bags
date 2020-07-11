package examples.tictactoe

import org.scalatest.{FlatSpec, Matchers}

class TicTacToeRuleSetTest extends FlatSpec with Matchers {

  behavior of "A tic-tac-toe ruleSet"
  val allValidMoves: Seq[TicTacToeMove] = for (x <- 0 until 3; y <- 0 until 3) yield Put((x, y))

  it should "generate all the available moves" in {
    implicit val ticTacToeState: TicTacToeState = TicTacToe.newGame
    ticTacToeState.ruleSet.availableMoves should contain theSameElementsAs (allValidMoves)
  }

  it should "remove a move after using it" in {
    implicit val ticTacToeState: TicTacToeState = TicTacToe.newGame
    val move: TicTacToeMove = ticTacToeState.ruleSet.availableMoves.head
    ticTacToeState executeMove move
    ticTacToeState.ruleSet.availableMoves should contain theSameElementsAs (allValidMoves filter (_ != move))
  }

  it should "accept all the valid moves" in {
    implicit val ticTacToeState: TicTacToeState = TicTacToe.newGame
    allValidMoves map (ticTacToeState.ruleSet.isValid(_)) reduce (_ && _) should be (true)
  }
}
