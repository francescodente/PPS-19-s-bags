package sbags.interaction.controller

import examples.tictactoe.TicTacToeState
import org.scalactic.Fail
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.core.{Board, Failure, Game, InvalidMove}
import sbags.interaction.view.{Event, GameView, LaneSelected, PawnSelected, Quit, TileSelected}

class GameControllerTest extends FlatSpec with Matchers with MockFactory {
  trait Move
  case class Put(x:Any, y:Any) extends Move
  trait State

  private val viewMock = mock[GameView[State]]
  private val gameMock = mock[Game[Move, State]]

  private def moves(events: List[Event]) = events match {
    case LaneSelected(x) :: LaneSelected(y) :: Nil => Some(Put(x, y))
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
  private def newInputListener =
    new GameController(viewMock, gameMock, moves)

  behavior of "A controller for a TicTacToe"

  it should "be able to Quit game" in {
    val inputListener = newInputListener
    (viewMock.stopGame _).expects().once()

    inputListener onEvent Quit
  }

  it should "perform a valid Move when asked" in {
    val inputListener = newInputListener
    (gameMock.executeMove _).expects(Put(*, *)).returns(Right(mock[State]))
    (viewMock.moveAccepted _).expects(*).once()
    (viewMock.nextCommand _).expects().once()

    inputListener onEvent TileSelected(1,1)
  }

  it should "wait commands till Move is finished" in {
    val inputListener = newInputListener
    (viewMock.nextCommand _).expects().once()

    inputListener onEvent LaneSelected(1)
  }

  it should "not perform an invalid Move" in {
    val inputListener = newInputListener
    (gameMock.executeMove _).expects(Put(*, *)).returns(Left(mock[Failure]))
    (viewMock.moveRejected _).expects(*).once()
    (viewMock.nextCommand _).expects().once()

    inputListener onEvent TileSelected(1,1)
  }

  it should "be able to perform multiple moves correctly" in {
    val inputListener = newInputListener
    inOrder(
      (gameMock.executeMove _).expects(Put(*, *)).returns(Right(mock[State])),
      (viewMock.moveAccepted _).expects(*),
      (viewMock.nextCommand _).expects(),
      (gameMock.executeMove _).expects(Put(*, *)).returns(Left(mock[Failure])),
      (viewMock.moveRejected _).expects(*),
      (viewMock.nextCommand _).expects()
    )

    inputListener onEvent TileSelected(1,1)
    inputListener onEvent TileSelected(1,2)
  }
}
