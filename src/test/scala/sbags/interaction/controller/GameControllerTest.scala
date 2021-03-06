package sbags.interaction.controller

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.interaction.view._
import sbags.model.core.{Failure, Game, InvalidMove}

class GameControllerTest extends FlatSpec with Matchers with MockFactory {

  trait Move

  case class Put(x: Int, y: Int) extends Move

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

  private val usualX = 1
  private val usualY = 2

  behavior of "A controller for a TicTacToe"

  it should "be able to Quit game" in {
    val inputListener = newInputListener
    (viewMock.stop _).expects().once()

    inputListener onEvent Quit
  }

  it should "handle valid undo commands" in {
    val inputListener = newInputListener
    val state = mock[State]
    (gameMock.undoLastMove _).expects().returns(Some(state)).once()
    (viewMock.moveAccepted _).expects(state).once()

    inputListener onEvent Undo
  }

  it should "handle invalid undo commands" in {
    val inputListener = newInputListener
    (gameMock.undoLastMove _).expects().returns(None).once()
    (viewMock.moveRejected _).expects(InvalidMove).once()

    inputListener onEvent Undo
  }

  it should "perform a valid Move when asked" in {
    val inputListener = newInputListener
    (gameMock.executeMove _).expects(Put(usualX, usualY)).returns(Right(mock[State]))
    (viewMock.moveAccepted _).expects(*).once()

    inputListener onEvent TileSelected(usualX, usualY)
  }

  it should "perform a valid composite Move when asked" in {
    val inputListener = newInputListener
    (gameMock.executeMove _).expects(Put(usualX, usualY)).returns(Right(mock[State]))
    (viewMock.moveAccepted _).expects(*).once()

    inputListener onEvent LaneSelected(usualX)
    inputListener onEvent LaneSelected(usualY)
  }

  it should "wait commands till Move is finished" in {
    val inputListener = newInputListener

    inputListener onEvent LaneSelected(usualX)
  }

  it should "not perform an invalid Move" in {
    val inputListener = newInputListener
    (gameMock.executeMove _).expects(Put(usualX, usualY)).returns(Left(mock[Failure]))
    (viewMock.moveRejected _).expects(*).once()

    inputListener onEvent TileSelected(usualX, usualY)
  }

  it should "be able to perform multiple moves correctly" in {
    val inputListener = newInputListener
    inOrder(
      (gameMock.executeMove _).expects(Put(usualX, usualY)).returns(Right(mock[State])),
      (viewMock.moveAccepted _).expects(*),
      (gameMock.executeMove _).expects(Put(usualX, usualY)).returns(Left(mock[Failure])),
      (viewMock.moveRejected _).expects(*)
    )

    inputListener onEvent TileSelected(usualX, usualY)
    inputListener onEvent TileSelected(usualX, usualY)
  }
}
