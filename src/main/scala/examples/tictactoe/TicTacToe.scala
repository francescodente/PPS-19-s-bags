package examples.tictactoe

import sbags.entity._

object TicTacToe extends GameDescription {
  type BoardState = TicTacToeBoard
  type GameState = TicTacToeState

  override def newGame: GameState = new TicTacToeState(new TicTacToeBoard)
}

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

class TicTacToeRuleSet extends RuleSet[TicTacToeMove, TicTacToeState] {
  override def availableMoves(implicit state: TicTacToeState): Seq[TicTacToeMove] =
    for (t <- state.boardState.tiles; if state.boardState(t).isEmpty) yield Put(t)

  override def executeMove(move: TicTacToeMove)(implicit state: TicTacToeState): Unit = move match {
    case Put(tile) =>
      state.boardState << (state.turn.get -> tile)
  }
}

class TicTacToeBoard extends BasicRectangularBoard(3, 3) {
  type Pawn = TicTacToePawn
}

sealed trait TicTacToeResult
case object Draw extends TicTacToeResult
case class Winner(pawn: TicTacToePawn) extends TicTacToeResult

sealed trait TicTacToePawn
case object X extends TicTacToePawn
case object O extends TicTacToePawn

sealed trait TicTacToeMove
case class Put(tile: (Int, Int)) extends TicTacToeMove
