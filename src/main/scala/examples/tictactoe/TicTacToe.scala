package examples.tictactoe

import sbags.core.extension._
import sbags.core.extension.Results.{Draw, WinOrDraw, Winner}
import sbags.core.dsl.RuleSetBuilder
import sbags.core.ruleset.RuleSet
import sbags.core.{Board, Coordinate, GameDescription, WinOrDrawCondition}

object TicTacToe extends GameDescription {
  val size = 3

  type Move = TicTacToeMove
  type State = TicTacToeState
  type BoardStructure = TicTacToeBoard.type

  override def initialState: State = TicTacToeState(Board(TicTacToeBoard), X)

  override val ruleSet: RuleSet[Move, State] = TicTacToeRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, TicTacToeState] =
    BoardState(_.board, (s, b) => s.copy(board = b))

  implicit lazy val turns: TurnState[TicTacToePawn, State] =
    TurnState(_.currentTurn, s => s.copy(currentTurn = TicTacToePawn.opponent(s.currentTurn)))

  implicit lazy val endCondition: WinOrDrawCondition[TicTacToePawn, TicTacToeState] =
    new WinOrDrawCondition[TicTacToePawn, State] {
      override def gameResult(state: State): Option[WinOrDraw[TicTacToePawn]] = {
        val result = TicTacToeBoard.allMainLanes.map(laneResult(state)).find(_.isDefined).flatten
        if (result.isEmpty && isFull(state))
          Some(Draw)
        else
          result map (Winner(_))
      }

      private def laneResult(state: State)(lane: Seq[Coordinate]): Option[TicTacToePawn] = {
        val distinct = lane.map(state.board(_)).distinct
        if (distinct.size == 1) distinct.head else None
      }

      private def isFull(state: State): Boolean =
        state.board.boardMap.size == TicTacToeBoard.size * TicTacToeBoard.size
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
