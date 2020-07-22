package examples.tictactoe

import sbags.core.BoardGameState._
import sbags.core.TurnState._
import sbags.core.Results.{Draw, WinOrDraw, Winner}
import sbags.core.dsl.RuleSetBuilder
import sbags.core.ruleset.RuleSet
import sbags.core.{Board, BoardGameState, Coordinate, GameDescription, TurnState, WinOrDrawCondition}

object TicTacToe extends GameDescription with RuleSetBuilder[TicTacToeMove, TicTacToeState] {
  val size = 3

  type Move = TicTacToeMove
  type State = TicTacToeState

  override def initialState: TicTacToeState = TicTacToeState(Board(TicTacToeBoard), X)

  override val ruleSet: RuleSet[Move, State] = ruleSet {
    onMove matching {
      case Put(t) => state =>
        val newBoard = state.board.place(state.currentTurn, t)
        val nextTurn = TicTacToePawn.opponent(state.currentTurn)
        state.setBoard(newBoard).setTurn(nextTurn)
    }

    moveGeneration { implicit context =>
      iterating over emptyTiles as { t =>
        generate move Put(t)
      }
    }
  }

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
        TicTacToeBoard.diagonals ++ TicTacToeBoard.rows ++ TicTacToeBoard.cols

      private def laneResult(state: TicTacToeState)(lane: Seq[Coordinate]): Option[TicTacToePawn] = {
        val distinct = lane.map(state.board(_)).distinct
        if (distinct.size == 1) distinct.head else None
      }

      private def isFull(state: TicTacToeState): Boolean =
        state.board.boardMap.size == TicTacToeBoard.size * TicTacToeBoard.size
    }

  implicit val turns: TurnState[TicTacToePawn, TicTacToeState] = new TurnState[TicTacToePawn, TicTacToeState] {
    override def turn(state: TicTacToeState): TicTacToePawn = state.currentTurn

    override def setTurn(state: TicTacToeState)(turn: TicTacToePawn): TicTacToeState = state.copy(currentTurn = turn)
  }
}
