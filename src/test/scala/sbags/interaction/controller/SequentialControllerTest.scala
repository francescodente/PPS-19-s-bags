package sbags.interaction.controller

import examples.tictactoe.{O, Put, TicTacToe, TicTacToePawn, TicTacToeState, X}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.core.GameEndCondition
import sbags.interaction.view.View

class SequentialControllerTest extends FlatSpec with Matchers with MockFactory {
  private val viewMock = mock[View[TicTacToe.State]]
  implicit val gameEndMock: GameEndCondition[_, TicTacToeState] = mock[GameEndCondition[TicTacToePawn,TicTacToe.State]]

  private def ticTacToeMoves(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }

  behavior of "A sequential controller for a non finished TicTacToe"

  it should "perform a Put when a tile is selected" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialController(viewMock, game, ticTacToeMoves)
    (viewMock.moveAccepted _).expects(*).once()
    (viewMock.nextCommand _).expects().twice()
    (gameEndMock.gameResult _).expects(*).returns(None).twice()

    inputListener notify TileSelected(1,1)
    inputListener notify Done

    game.currentState.board(1,1) should be (Some(X))
  }

  it should "not perform any move if a sequence of events isn't terminated" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialController(viewMock, game, ticTacToeMoves)
    val initialBoardState = game.currentState.board
    (viewMock.moveAccepted _).expects(*).never()
    (viewMock.nextCommand _).expects().once()
    (gameEndMock.gameResult _).expects(*).returns(None).once()

    inputListener notify TileSelected(1,1)

    game.currentState.board should be (initialBoardState)
  }

  it should "not perform any move if two moves in a row are submitted without a Done in between them" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialController(viewMock, game, ticTacToeMoves)
    val initialBoardState = game.currentState.board
    (viewMock.moveRejected _).expects().once()
    (viewMock.nextCommand _).expects() repeated 3 times()
    (gameEndMock.gameResult _).expects(*).returns(None) repeated 3 times()

    inputListener notify TileSelected(1,1)
    inputListener notify TileSelected(1,2)
    inputListener notify Done

    game.currentState.board should be (initialBoardState)
  }

  it should "be able to perform multiple moves correctly" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialController(viewMock, game, ticTacToeMoves)
    (viewMock.moveAccepted _).expects(*).twice()
    (viewMock.nextCommand _).expects() repeated 4 times()
    (gameEndMock.gameResult _).expects(*).returns(None) repeated 4 times()

    inputListener notify TileSelected(1,1)
    inputListener notify Done
    inputListener notify TileSelected(1,2)
    inputListener notify Done

    game.currentState.board(1,2) should be (Some(O))
  }

  behavior of "A sequential controller for a finished TicTacToe"

  it should "shutdown the view when asked to start a game" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialController(viewMock, game, ticTacToeMoves)
    (viewMock.shutDown _).expects().once()
    (gameEndMock.gameResult _).expects(*).returns(Some(*)).once()

    inputListener.startGame()
  }

  it should "shutdown the view at the first event notified" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialController(viewMock, game, ticTacToeMoves)
    (viewMock.shutDown _).expects().once()
    (gameEndMock.gameResult _).expects(*).returns(Some(*)).once()

    inputListener notify TileSelected(1,1)
  }
}
