package examples.connectfour

import org.scalatest.{FlatSpec, Matchers}

class ConnectFourRuleSetTest extends FlatSpec with Matchers {
  val allValidMoves: Seq[ConnectFourMove] = for (x <- 0 until ConnectFour.width) yield Put(x)

  behavior of "A connect four ruleSet"

  it should "generate all the available moves" in {
    val game = ConnectFour.newGame
    ConnectFour.ruleSet.availableMoves(game.currentState) should contain theSameElementsAs allValidMoves
  }

  it should "remove a move after a column is full" in {
    val game = ConnectFour.newGame
    val move: ConnectFourMove = ConnectFour.ruleSet.availableMoves(game.currentState).head
    (0 until ConnectFour.height).foreach(_ => game executeMove move)
    ConnectFour.ruleSet.availableMoves(game.currentState) should contain theSameElementsAs (allValidMoves filter (_ != move))
  }

  it should "accept all the valid moves" in {
    val game = ConnectFour.newGame
    allValidMoves map (ConnectFour.ruleSet.isValid(_)(game.currentState)) reduce (_ && _) should be(true)
  }
}
