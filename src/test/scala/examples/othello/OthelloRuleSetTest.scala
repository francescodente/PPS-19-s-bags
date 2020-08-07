package examples.othello

import org.scalatest.{FlatSpec, Matchers}
import sbags.core.InvalidMove

class OthelloRuleSetTest extends FlatSpec with Matchers {
  behavior of "An Othello rule set"

  it should "not allow a player to place a pawn on an occupied tile" in {
    val game = Othello.newGame
    game executeMove Put((3, 3)) should be (Left(InvalidMove))
  }
}
