package examples.tictactoe

import sbags.entity._

/**
 * TicTacToePawn represents TicTacToe's pawns.
 */
sealed trait TicTacToePawn

/**
 * Represents the X in a TicTacToe game.
 */
case object X extends TicTacToePawn

/**
 * Represents the O in a TicTacToe game.
 */
case object O extends TicTacToePawn

/**
 * TicTacToeResult represents all the possibles TicTacToc end game result.
 */
sealed trait TicTacToeResult

/**
 * Represents a draw in a TicTacToe's game.
 */
case object Draw extends TicTacToeResult

/**
 * Represents the winner of TicTacToe game.
 *
 * @param pawn the type of pawn that wins the game.
 */
case class Winner(pawn: TicTacToePawn) extends TicTacToeResult

/**
 * Represents the type of moves available in a game.
 */
sealed trait TicTacToeMove

/**
 * A move that put X or O in the given position, based on the rules of the game and the current turn.
 *
 * @param tile the tile that represents the position on the game board.
 */
case class Put(tile: (Int, Int)) extends TicTacToeMove

/**
 * Represents the TicTacToe board,
 * with size 3x3
 * and Pawn of type [[examples.tictactoe.TicTacToePawn]].
 */
class TicTacToeBoard extends BasicRectangularBoard(3, 3) {
  type Pawn = TicTacToePawn
}

/**
 * Represents the state of a TicTacToe game,
 * declare the Move type
 * support two alternate Players,
 * end the turn after every Move,
 * declare the game end conditions.
 *
 * @param board the board of the game.
 */
class TicTacToeState(board: TicTacToeBoard)
  extends BasicGameState[TicTacToeBoard](board)
    with TwoPlayersAlternateTurn[TicTacToePawn]
    with EndTurnAfterEachMove[TicTacToePawn]
    with GameEndCondition[TicTacToeResult] {

  type Move = TicTacToeMove

  val playersPair: (TicTacToePawn, TicTacToePawn) = (X, O)

  val ruleSet: RuleSet[TicTacToeMove, this.type] = new TicTacToeRuleSet

  private def checkTris(list: Seq[(Int, Int)]): Boolean = {
    val rows = list.groupBy(_._1).values.toList
    val cols = list.groupBy(_._2).values.toList
    val leftRightDiagonal = list.filter(m => m._1 == m._2)
    val rightLeftDiagonal = list.filter(m => m._1 == 2 - m._2)
    val lanes = leftRightDiagonal :: rightLeftDiagonal :: rows ++ cols
    lanes.exists(_.size == 3)
  }

  override def gameResult: Option[TicTacToeResult] = {
    val result = boardState.tiles.filter(boardState(_).isDefined)
      .groupBy(t => boardState(t).get).find {
      case (X, l) if checkTris(l) => true
      case (O, l) if checkTris(l) => true
      case _ => false
    }
    result map (t => Winner(t._1))
  }
}

/**
 * Represents the rules of a TicTacToe game.
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
 * Describe how to create a new TicTacToe game.
 */
object TicTacToe extends GameDescription {
  type BoardState = TicTacToeBoard
  type GameState = TicTacToeState

  override def newGame: GameState = new TicTacToeState(new TicTacToeBoard)
}
