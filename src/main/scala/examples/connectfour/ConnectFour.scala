package examples.connectfour

import sbags.core.extension.Results.{Draw, WinOrDraw, Winner}
import sbags.core.{Board, Coordinate, GameDescription, WinOrDrawCondition}
import sbags.core.dsl.RuleSetBuilder
import sbags.core.ruleset.RuleSet
import sbags.core.extension._

import scala.annotation.tailrec

object ConnectFour extends GameDescription {
  val width = 7
  val height = 6
  val connectedToWin = 4

  type Move = ConnectFourMove
  type State = ConnectFourState
  type BoardStructure = ConnectFourBoard.type

  override def initialState: State = ConnectFourState(Board(ConnectFourBoard), Seq(Red, Blue))

  override val ruleSet: RuleSet[Move, State] = TicTacToeRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((s, b) => s.copy(board = b))

  implicit lazy val turns: PlayersAsTurns[ConnectFourPawn, State] =
    PlayersAsTurns.roundRobin((s, seq) => s.copy(players = seq))

  implicit lazy val endCondition: WinOrDrawCondition[ConnectFourPawn, State] =
    new WinOrDrawCondition[ConnectFourPawn, State] {
      override def gameResult(state: State): Option[WinOrDraw[ConnectFourPawn]] = {
        val winnableLanes = ConnectFourBoard.allLanes
          .flatMap(l => divideIn(l, Seq.empty)(connectedToWin))
          .filter(_.size == connectedToWin)
        val result = winnableLanes.map(laneResult(state)).find(_.isDefined).flatten
        if (result.isEmpty && isFull(state))
          Some(Draw)
        else
          result map (Winner(_))
      }

      @tailrec
      private def divideIn(lane: Stream[Coordinate], accumulator: Seq[Seq[Coordinate]])(divisor: Int): Seq[Seq[Coordinate]] = lane match {
        case head #:: tl if tl.size >= divisor - 1 => divideIn(tl, Seq(head #:: tl.take(divisor - 1)) ++: accumulator)(divisor)
        case _ => accumulator
      }

      private def laneResult(state: State)(lane: Seq[Coordinate]): Option[ConnectFourPawn] = {
        val distinct = lane.map(state.board(_)).distinct
        if (distinct.size == 1) distinct.head else None
      }

      private def isFull(state: State): Boolean =
        state.board.boardMap.size == ConnectFourBoard.width * ConnectFourBoard.height
    }

  object TicTacToeRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
    onMove matching {
      case Put(x) => state =>
        val emptyTiles = ConnectFourBoard.rows.flatten.filter(coordinate => coordinate.x == x && state.board(coordinate).isEmpty)
        val firstYEmpty = emptyTiles.foldLeft(0)((maxY, coordinate) => if (coordinate.y > maxY) coordinate.y else maxY)
        val newBoard = state.board.place(state.currentPlayer, (x, firstYEmpty))
        state.setBoard(newBoard).nextTurn()
    }

    moveGeneration {
      iterating over emptyTiles as { t =>
        generate(Put(t.x))
      }
    }
  }
}
