package examples.tictactoe

import sbags.core.BoardGameState._
import sbags.core.TurnState._
import sbags.core.Results.{Draw, WinOrDraw, Winner}
import sbags.core.dsl.RuleSetBuilder
import sbags.core.ruleset.RuleSet
import sbags.core.{Board, BoardGameState, Coordinate, GameDescription, TurnState, WinOrDrawCondition}

object TicTacToe extends GameDescription {
  val size = 3

  type Move = TicTacToeMove
  type State = TicTacToeState

  override def initialState: TicTacToeState = TicTacToeState(Board(TicTacToeBoard), X)

  override val ruleSet: RuleSet[Move, State] = TicTacToeRuleSet

  implicit val boardState: BoardGameState[TicTacToeBoard.type, TicTacToeState] =
    new BoardGameState[TicTacToeBoard.type, TicTacToeState] {
      override def boardState(state: TicTacToeState): Board[TicTacToeBoard.type] =
        state.board

      override def setBoard(state: TicTacToeState)(board: Board[TicTacToeBoard.type]): TicTacToeState =
        state.copy(board = board)
    }

  implicit val endCondition: WinOrDrawCondition[TicTacToePawn, TicTacToeState] =
    new WinOrDrawCondition[TicTacToePawn, TicTacToeState] {
      override def gameResult(state: TicTacToeState): Option[WinOrDraw[TicTacToePawn]] = {
        val result = allLanes.map(laneResult(state)).find(_.isDefined).flatten
        if (result.isEmpty && isFull(state))
          Some(Draw)
        else
          result map (Winner(_))
      }

      private def allLanes: Stream[Seq[Coordinate]] =
        TicTacToeBoard.mainDiagonals ++ TicTacToeBoard.rows ++ TicTacToeBoard.cols

      private def laneResult(state: TicTacToeState)(lane: Seq[Coordinate]): Option[TicTacToePawn] = {
        val distinct = lane.map(state.board(_)).distinct
        if (distinct.size == 1) distinct.head else None
      }

      private def isFull(state: TicTacToeState): Boolean =
        state.board.boardMap.size == TicTacToeBoard.size * TicTacToeBoard.size
    }

  implicit val turns: TurnState[TicTacToePawn, TicTacToeState] = new TurnState[TicTacToePawn, TicTacToeState] {
    override def turn(state: TicTacToeState): TicTacToePawn = state.currentTurn

    override def nextTurn(state: TicTacToeState): TicTacToeState = state.copy(currentTurn = TicTacToePawn.opponent(state.currentTurn))
  }

  object TicTacToeRuleSet extends RuleSet[TicTacToeMove, TicTacToeState] with RuleSetBuilder[TicTacToeMove, TicTacToeState] {
    onMove matching {
      case Put(t) => state =>
        val newBoard = state.board.place(state.currentTurn, t)
        state.setBoard(newBoard).nextTurn()
    }

    moveGeneration { implicit context =>
      iterating over emptyTiles as { t =>
        generate (Put(t))
      }
    }
  }
}
