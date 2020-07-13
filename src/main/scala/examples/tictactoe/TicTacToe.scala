package examples.tictactoe

/**
 * This is an example of the use of the library.
 * It's an implementation of the classic game Tic Tac Toe.
 */

import sbags.core.Results.{Draw, WinOrDraw, Winner}
import sbags.core._

/**
 * Represents TicTacToe Pawns.
 */
sealed trait TicTacToePawn

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
class TicTacToeBoard extends BasicSquareBoard(TicTacToe.size) {
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
 * @param ruleSet the TicTacToe rule set.
 */
class TicTacToeState(board: TicTacToeBoard, val ruleSet: TicTacToeRuleSet)
  extends BasicGameState(board)
    with TwoPlayersAlternateTurn[TicTacToePawn]
    with EndTurnAfterEachMove[TicTacToePawn]
    with WinOrDrawCondition[TicTacToePawn] {

  type Move = TicTacToeMove
  type Rules = TicTacToeRuleSet

  val playersPair: (TicTacToePawn, TicTacToePawn) = (X, O)

  override def gameResult: Option[WinOrDraw[TicTacToePawn]] = {
    val result = allLanes.map(laneResult).find(_.isDefined).flatten
    if (result.isEmpty && isFull)
      Some(Draw)
    else
      result map (Winner(_))
  }

  private def allLanes: Stream[Seq[Coordinate]] =
    boardState.diagonals ++ boardState.rows ++ boardState.cols

  private def laneResult(lane: Seq[Coordinate]): Option[TicTacToePawn] = {
    val distinct = lane.map(boardState(_)).distinct
    if (distinct.size == 1) distinct.head else None
  }

  private def isFull: Boolean = boardState.boardMap.size == boardState.size * boardState.size
}

/**
 * Represents the rules of TicTacToe.
 */
class TicTacToeRuleSet extends RuleSet[TicTacToeMove, TicTacToeState] {
  override def availableMoves(implicit state: TicTacToeState): Seq[TicTacToeMove] =
    for (t <- state.boardState.tiles; if state.boardState(t).isEmpty) yield Put(t)

  override def executeMove(move: TicTacToeMove)(implicit state: TicTacToeState): Unit = move match {
    case Put(tile) => state.boardState << (state.turn.get -> tile)
  }
}

/**
 * Describes how to create a new TicTacToe game.
 */
object TicTacToe extends GameDescription[TicTacToeState] {
  val size = 3
  override def newGame: TicTacToeState = new TicTacToeState(new TicTacToeBoard, new TicTacToeRuleSet)
}
