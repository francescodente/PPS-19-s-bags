package examples.tictactoe

/**
 * This is an example of the use of the library.
 * It's an implementation of the classic game Tic Tac Toe.
 */

import sbags.core.Results.{Draw, WinOrDraw, Winner}
import sbags.core._
import sbags.core.ruleset.RuleSet

/**
 * Represents TicTacToe Pawns.
 */
sealed trait TicTacToePawn

object TicTacToePawn {
  def opponent(pawn: TicTacToePawn): TicTacToePawn = pawn match {
    case X => O
    case O => X
  }
}

/**
 * Represents the X in TicTacToe.
 */
case object X extends TicTacToePawn

/**
 * Represents the O in TicTacToe.
 */
case object O extends TicTacToePawn

/**
 * Represents the type of moves available in TicTacToe.
 */
sealed trait TicTacToeMove

/**
 * A move that puts X or O in the given position, based on the rule set of TicTacToe and the current turn.
 *
 * @param tile the tile that represents the position on the game board.
 */
case class Put(tile: Coordinate) extends TicTacToeMove

/**
 * Represents the TicTacToe board,
 * with size 3x3
 * and Pawn of type [[examples.tictactoe.TicTacToePawn]].
 */
object TicTacToeBoard extends SquareBoard(TicTacToe.size) {
  type Pawn = TicTacToePawn
}

/**
 * Represents the state of a TicTacToe game,
 * declares the type of Move,
 * supports two alternate Players,
 * ends the turn after every Move,
 * declares the game's end conditions.
 *
 * @param board   the board of the game.
 */
case class TicTacToeState(board: Board[TicTacToeBoard.type], currentTurn: TicTacToePawn)
  extends BasicGameState(board)
    with Turns[TicTacToePawn]
    with WinOrDrawCondition[TicTacToePawn] {
  override def gameResult: Option[WinOrDraw[TicTacToePawn]] = {
    val result = allLanes.map(laneResult).find(_.isDefined).flatten
    if (result.isEmpty && isFull)
      Some(Draw)
    else
      result map (Winner(_))
  }

  private def allLanes: Stream[Seq[Coordinate]] =
    boardState.structure.diagonals ++ boardState.structure.rows ++ boardState.structure.cols

  private def laneResult(lane: Seq[Coordinate]): Option[TicTacToePawn] = {
    val distinct = lane.map(boardState(_)).distinct
    if (distinct.size == 1) distinct.head else None
  }

  private def isFull: Boolean = boardState.boardMap.size == boardState.structure.size * boardState.structure.size

  override def turn: TicTacToePawn = currentTurn

  override def players: Set[TicTacToePawn] = Set(X, O)
}

/**
 * Represents the rules of TicTacToe.
 */
object TicTacToeRuleSet extends RuleSet[TicTacToeMove, TicTacToeState] {
  override def availableMoves(state: TicTacToeState): Seq[TicTacToeMove] =
    for (t <- state.boardState.structure.tiles; if state.boardState(t).isEmpty) yield Put(t)

  override def executeMove(move: TicTacToeMove)(state: TicTacToeState): TicTacToeState = move match {
    case Put(tile) =>
      val board = state.boardState place (state.turn, tile)
      val nextTurn = TicTacToePawn.opponent(state.turn)
      TicTacToeState(board, nextTurn)
  }
}

/**
 * Describes how to create a new TicTacToe game.
 */
object TicTacToe extends GameDescription {
  val size = 3
  type State = TicTacToeState
  type Move = TicTacToeMove
  override def initialState: TicTacToeState = TicTacToeState(Board(TicTacToeBoard), X)

  override val ruleSet: RuleSet[TicTacToeMove, TicTacToeState] = TicTacToeRuleSet
}
