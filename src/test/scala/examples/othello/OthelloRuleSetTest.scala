package examples.othello

import org.scalatest.{FlatSpec, Matchers}
import examples.othello.Othello._
import examples.othello.OthelloRuleSet._
import sbags.model.core.{Board, Coordinate}

class OthelloRuleSetTest extends FlatSpec with Matchers {
  private def newEmptyBoard: Board[BoardStructure] = Board(OthelloBoard)

  private def line(origin: Coordinate, direction: (Int, Int), length: Int): Stream[Coordinate] =
    Stream.iterate(origin, length)(_ + direction)

  implicit class BoardUtils(board: Board[BoardStructure]) {
    def placeAll(pawn: OthelloPawn)
                (tiles: Stream[Coordinate],
                 first: OthelloPawn = pawn,
                 last: OthelloPawn = pawn): Board[BoardStructure] = {
      val pawns: Stream[OthelloPawn] = first #:: tiles.map(_ => pawn).drop(2) :+ last
      tiles.zip(pawns).foldLeft(board)((b, p) => b.place(p._2, p._1))
    }
  }

  private val origin: Coordinate = (3, 3)
  private val lineLength: Int = 3
  private val right: (Int, Int) = (1, 0)
  private val down: (Int, Int) = (0, 1)
  private val downRight: (Int, Int) = (1, 1)
  private val horizontal: Stream[Coordinate] = line(origin + right, right, lineLength)
  private val vertical: Stream[Coordinate] = line(origin + down, down, lineLength)
  private val diagonal: Stream[Coordinate] = line(origin + downRight, downRight, lineLength)

  behavior of "Othello move generation"

  it should "generate capture moves only" in {
    val board = newEmptyBoard
      .place(Black, origin)
      .placeAll(White)(horizontal)
      .placeAll(White)(vertical)
      .placeAll(White)(diagonal)
    val state = OthelloState(board, Black)
    availableMoves(state) should contain theSameElementsAs Seq(
      Put(horizontal.last + right), Put(diagonal.last + downRight), Put(vertical.last + down)
    )
  }

  it should "generate no moves if no capture is available" in {
    val board = newEmptyBoard
      .place(White, origin)
    val state = OthelloState(board, Black)
    availableMoves(state) should be (empty)
  }

  it should "generate no moves if the board is full" in {
    var board = newEmptyBoard
    OthelloBoard.tiles.foreach(t => board = board.place(White, t))
    val state = OthelloState(board, Black)
    availableMoves(state) should be (empty)
  }

  behavior of "Othello's Put execution"

  it should "place the active player's pawn" in {
    val board = newEmptyBoard
    val state = OthelloState(board, Black)
    val newState = executeMove(Put(origin))(state)
    newState.board(origin) should be (Some(Black))
  }

  it should "change turn if the opponent has moves available" in {
    val board = newEmptyBoard
      .placeAll(White)(horizontal, first = Black, last = Black)
    val state = OthelloState(board, White)
    val newState = executeMove(Put(origin))(state)
    newState.currentPlayer should be (Black)
  }

  it should "flip all in-between pawns" in {
    val board = newEmptyBoard
      .placeAll(White)(horizontal, last = Black)
      .placeAll(White)(vertical, last = Black)
      .placeAll(White)(diagonal, last = Black)
    val state = OthelloState(board, Black)
    val newState = executeMove(Put(origin))(state)
    horizontal ++ vertical ++ diagonal map (newState.board(_).get) should contain only Black
  }

  it should "not change turn if the opponent has no moves available" in {
    val board = newEmptyBoard
      .placeAll(White)(horizontal, last = Black)
    val state = OthelloState(board, Black)
    val newState = executeMove(Put(origin))(state)
    newState.currentPlayer should be (Black)
  }
}
