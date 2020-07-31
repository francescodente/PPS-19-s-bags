package sbags.interaction.controller

import examples.tictactoe.{O, Put, TicTacToe, TicTacToePawn, TicTacToeState, X}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.core.extension.GameEndCondition
import sbags.interaction.view.GameView

class SequentialGameControllerTest extends FlatSpec with Matchers with MockFactory {
  private val viewMock = mock[GameView[TicTacToe.State]]
  implicit val gameEndMock: GameEndCondition[_, TicTacToeState] = mock[GameEndCondition[TicTacToePawn,TicTacToe.State]]

  private def ticTacToeMoves(events: List[Event]) = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }

  behavior of "A sequential controller for a non finished TicTacToe"

  it should "perform a Put when a tile is selected" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialGameController(viewMock, game, ticTacToeMoves)
    (viewMock.moveAccepted _).expects(*).once()
    (viewMock.nextCommand _).expects().once()
    (gameEndMock.gameResult _).expects(*).returns(None).once()

    inputListener notify TileSelected(1,1)

    game.currentState.board(1,1) should be (Some(X))
  }

  it should "not perform any move if a sequence of events isn't terminated" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialGameController(viewMock, game, ticTacToeMoves)
    val initialBoardState = game.currentState.board
    (viewMock.moveAccepted _).expects(*).never()
    (viewMock.nextCommand _).expects().once()
    (gameEndMock.gameResult _).expects(*).returns(None).once()

    inputListener notify PawnSelected("a")

    game.currentState.board should be (initialBoardState)
  }

  it should "be able to perform multiple moves correctly" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialGameController(viewMock, game, ticTacToeMoves)
    (viewMock.moveAccepted _).expects(*).twice()
    (viewMock.nextCommand _).expects() repeated 2 times()
    (gameEndMock.gameResult _).expects(*).returns(None) repeated 2 times()

    inputListener notify TileSelected(1,1)
    inputListener notify TileSelected(1,2)

    game.currentState.board(1,2) should be (Some(O))
  }

  it should "reject invalid moves" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialGameController(viewMock, game, ticTacToeMoves)
    (viewMock.moveAccepted _).expects(*).once()
    (viewMock.moveRejected _).expects().once()
    (viewMock.nextCommand _).expects() repeated 2 times()
    (gameEndMock.gameResult _).expects(*).returns(None) repeated 2 times()

    inputListener notify TileSelected(1,1)
    inputListener notify TileSelected(1,1)
  }

  behavior of "A sequential controller for a finished TicTacToe game"

  it should "stop the view when asked to start a game" in {
    val game = TicTacToe.newGame
    val inputListener = new SequentialGameController(viewMock, game, ticTacToeMoves)
    (viewMock.stopGame _).expects().once()
    (gameEndMock.gameResult _).expects(*).returns(Some(*)).once() //The game result will always be present, this means the game is ended.

    inputListener.startGame()
  }
}
