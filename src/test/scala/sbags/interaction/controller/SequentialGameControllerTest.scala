package sbags.interaction.controller

import examples.tictactoe._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.core.{Game, InvalidMove}
import sbags.interaction.view.{Event, GameView, PawnSelected, TileSelected}

class SequentialGameControllerTest extends FlatSpec with Matchers with MockFactory {
  private val viewMock = mock[GameView[TicTacToe.State]]

  private def ticTacToeMoves(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
  private def newInputListener(game: Game[TicTacToeMove, TicTacToeState]) =
    new SequentialGameController(viewMock, game, ticTacToeMoves)

  behavior of "A sequential controller for a non finished TicTacToe"

  it should "perform a Put when a tile is selected" in {
    val game = TicTacToe.newGame
    val inputListener = newInputListener(game)
    (viewMock.moveAccepted _).expects(*).once()
    (viewMock.nextCommand _).expects().once()

    inputListener onEvent TileSelected(1,1)

    game.currentState.board(1,1) should be (Some(X))
  }

  it should "not perform any move if a sequence of events isn't terminated" in {
    val game = TicTacToe.newGame
    val inputListener = newInputListener(game)
    val initialBoardState = game.currentState.board
    (viewMock.moveAccepted _).expects(*).never()
    (viewMock.nextCommand _).expects().once()

    inputListener onEvent PawnSelected("a")

    game.currentState.board should be (initialBoardState)
  }

  it should "be able to perform multiple moves correctly" in {
    val game = TicTacToe.newGame
    val inputListener = newInputListener(game)
    (viewMock.moveAccepted _).expects(*).twice()
    (viewMock.nextCommand _).expects() repeated 2 times()

    inputListener onEvent TileSelected(1,1)
    inputListener onEvent TileSelected(1,2)

    game.currentState.board(1,2) should be (Some(O))
  }

  it should "reject invalid moves" in {
    val game = TicTacToe.newGame
    val inputListener = newInputListener(game)
    (viewMock.moveAccepted _).expects(*).once()
    (viewMock.moveRejected _).expects(InvalidMove).once()
    (viewMock.nextCommand _).expects() repeated 2 times()

    inputListener onEvent TileSelected(1,1)
    inputListener onEvent TileSelected(1,1)
  }
}
