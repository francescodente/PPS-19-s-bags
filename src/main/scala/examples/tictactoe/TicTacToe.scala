package examples.tictactoe

import sbags.core.extension._
import sbags.core.extension.Results.{Draw, WinOrDraw, Winner}
import sbags.core.dsl.RuleSetBuilder
import sbags.core.ruleset.RuleSet
import sbags.core.{Board, Coordinate, GameDescription}

object TicTacToe extends GameDescription[TicTacToeMove, TicTacToeState] {
  val size = 3
  private val players: Seq[TicTacToePawn] = Seq(X, O)

  type BoardStructure = TicTacToeBoard.type

  override def initialState: State = TicTacToeState(Board(TicTacToeBoard), X)

  override val ruleSet: RuleSet[Move, State] = TicTacToeRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((s, b) => s.copy(board = b))

  implicit lazy val turns: PlayersAsTurns[BoardStructure#Pawn, State] =
    PlayersAsTurns.roundRobin(_ => players, (s,p) => s.copy(currentPlayer = p))

  implicit lazy val endCondition: WinOrDrawCondition[BoardStructure#Pawn, State] =
    new WinOrDrawCondition[BoardStructure#Pawn, State] {
      override def gameResult(state: State): Option[WinOrDraw[BoardStructure#Pawn]] = {
        val result = TicTacToeBoard.allMainLanes.map(laneResult(state)).find(_.isDefined).flatten
        if (result.isEmpty && state.board.isFull)
          Some(Draw)
        else
          result map (Winner(_))
      }

      private def laneResult(state: State)(lane: Seq[Coordinate]): Option[BoardStructure#Pawn] = {
        val distinct = lane.map(state.board(_)).distinct
        if (distinct.size == 1) distinct.head else None
      }
    }

  object TicTacToeRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
    onMove matching {
      case Put(t) =>
        > place currentTurn on t
    }

    after each move -> changeTurn

    moveGeneration {
      iterating over emptyTiles as { t =>
        generate (Put(t))
      }
    }
  }
}
