package examples.tictactoe

import org.scalatest.{FlatSpec, Matchers}

class TicTacToeRuleSetTest extends FlatSpec with Matchers {
  val allValidMoves: Seq[TicTacToeMove] = for (x <- 0 until 3; y <- 0 until 3) yield Put((x, y))

  behavior of "A tic-tac-toe ruleSet"

  it should "generate all the available moves" in {
    val game = TicTacToe.newGame
    TicTacToe.ruleSet.availableMoves(game.currentState) should contain theSameElementsAs allValidMoves
  }

  it should "remove a move after using it" in {
    val game = TicTacToe.newGame
    val move: TicTacToeMove = TicTacToe.ruleSet.availableMoves(game.currentState).head
    game executeMove move
    TicTacToe.ruleSet.availableMoves(game.currentState) should contain theSameElementsAs (allValidMoves filter (_ != move))
  }

  it should "accept all the valid moves" in {
    val game = TicTacToe.newGame
    allValidMoves map (TicTacToe.ruleSet.isValid(_)(game.currentState)) reduce (_ && _) should be (true)
  }
}
