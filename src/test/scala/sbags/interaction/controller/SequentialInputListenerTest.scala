package sbags.interaction.controller

import examples.tictactoe.{Put, TicTacToe, X}
import org.scalatest.{FlatSpec, Matchers}

class SequentialInputListenerTest extends FlatSpec with Matchers {

  behavior of "A sequential input listener for TicTacToe"

  def ticTacToeMoves(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }

  it should "perform a Put when a tile is selected" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialInputListener[TicTacToe.State, TicTacToe.Move](game, ticTacToeMoves)

    inputListener notify TileSelected(1,1)
    inputListener notify Done

    game.currentState.board(1,1) should be (Some(X))
  }

}
