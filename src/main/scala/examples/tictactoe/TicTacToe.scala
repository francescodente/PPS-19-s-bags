package examples.tictactoe
/**
 * This is an example of the use of the library.
 * It's an implementation of the classic game Tic Tac Toe.
 */

import sbags.entity._

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
 * Represents all the possible TicTacToe's end game results.
 */
sealed trait TicTacToeResult

/**
 * Represents draw in TicTacToe.
 */
case object Draw extends TicTacToeResult

/**
 * Represents the winner of TicTacToe.
 *
 * @param pawn the pawn that wins the game.
 */
case class Winner(pawn: TicTacToePawn) extends TicTacToeResult

/**
 * Represents the type of moves available in TicTacToe.
 */
sealed trait TicTacToeMove

/**
 * A move that puts X or O in the given position, based on the rule set of TicTacToe and the current turn.
 *
 * @param tile the tile that represents the position on the game board.
 */
case class Put(tile: (Int, Int)) extends TicTacToeMove

/**
 * Represents the TicTacToe board,
 * with size 3x3
 * and Pawn of type [[examples.tictactoe.TicTacToePawn]].
 */
class TicTacToeBoard extends BasicRectangularBoard(TicTacToe.size, TicTacToe.size) {
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
  extends BasicGameState[TicTacToeBoard](board)
    with TwoPlayersAlternateTurn[TicTacToePawn]
    with EndTurnAfterEachMove[TicTacToePawn]
    with GameEndCondition[TicTacToeResult] {

  type Move = TicTacToeMove

  val playersPair: (TicTacToePawn, TicTacToePawn) = (X, O)

  private def checkTris(list: Seq[(Int, Int)]): Boolean = {
    val rows = list.groupBy(_._1).values.toList
    val cols = list.groupBy(_._2).values.toList
    val leftRightDiagonal = list.filter(m => m._1 == m._2)
    val rightLeftDiagonal = list.filter(m => m._1 == TicTacToe.size - 1 - m._2)
    val lanes = leftRightDiagonal :: rightLeftDiagonal :: rows ++ cols
    lanes.exists(_.size == TicTacToe.size)
  }

  override def gameResult: Option[TicTacToeResult] = {
    val result = boardState.tiles
      .filter(boardState(_).isDefined)
      .groupBy(t => boardState(t).get)
      .find {
        case (X, l) if checkTris(l) => true
        case (O, l) if checkTris(l) => true
        case _ => false
      }
    if (boardState.boardMap.size == TicTacToe.size*TicTacToe.size && result.isEmpty) Some(Draw)
    else result map (t => Winner(t._1))
  }
}

/**
 * Represents the rules of TicTacToe.
 */
class TicTacToeRuleSet extends RuleSet[TicTacToeMove, TicTacToeState] {
  override def availableMoves(implicit state: TicTacToeState): Seq[TicTacToeMove] =
    for (t <- state.boardState.tiles; if state.boardState(t).isEmpty) yield Put(t)

  override def executeMove(move: TicTacToeMove)(implicit state: TicTacToeState): Unit = move match {
    case Put(tile) =>
      state.boardState << (state.turn.get -> tile)
  }
}

/**
 * Describes how to create a new TicTacToe game.
 */
object TicTacToe extends GameDescription[TicTacToeState] {
  val size = 3
  override def newGame: TicTacToeState = new TicTacToeState(new TicTacToeBoard, new TicTacToeRuleSet)
}
