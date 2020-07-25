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
  type BoardStructure = TicTacToeBoard.type

  override def initialState: State = TicTacToeState(Board(TicTacToeBoard), X)

  override val ruleSet: RuleSet[Move, State] = TicTacToeRuleSet

  implicit object BoardState extends BoardGameState[BoardStructure, State] {
    override def boardState(state: State): Board[BoardStructure] =
      state.board

    override def setBoard(state: State)(board: Board[BoardStructure]): State =
      state.copy(board = board)
  }

  implicit object EndCondition extends WinOrDrawCondition[TicTacToePawn, State] {
    override def gameResult(state: State): Option[WinOrDraw[TicTacToePawn]] = {
      val result = allLanes.map(laneResult(state)).find(_.isDefined).flatten
      if (result.isEmpty && isFull(state))
        Some(Draw)
      else
        result map (Winner(_))
    }

    private def allLanes: Stream[Seq[Coordinate]] =
      TicTacToeBoard.mainDiagonals ++ TicTacToeBoard.rows ++ TicTacToeBoard.cols

    private def laneResult(state: State)(lane: Seq[Coordinate]): Option[TicTacToePawn] = {
      val distinct = lane.map(state.board(_)).distinct
      if (distinct.size == 1) distinct.head else None
    }

    private def isFull(state: State): Boolean =
      state.board.boardMap.size == TicTacToeBoard.size * TicTacToeBoard.size
  }

  implicit object Turns extends TurnState[TicTacToePawn, State] {
    override def turn(state: State): TicTacToePawn = state.currentTurn

    override def nextTurn(state: State): State =
      state.copy(currentTurn = TicTacToePawn.opponent(state.currentTurn))
  }

  object TicTacToeRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
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
