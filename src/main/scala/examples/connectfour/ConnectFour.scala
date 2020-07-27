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

  override def initialState: State = ConnectFourState(Board(ConnectFourBoard), Red)

  override val ruleSet: RuleSet[Move, State] = TicTacToeRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState(_.board, (s, b) => s.copy(board = b))

  implicit lazy val endCondition: WinOrDrawCondition[ConnectFourPawn, State] =
    new WinOrDrawCondition[ConnectFourPawn, State] {
      override def gameResult(state: State): Option[WinOrDraw[ConnectFourPawn]] = {
        val dividedLanes = ConnectFourBoard.allLanes.flatMap(l => divideIn(l.toList, Seq.empty)(connectedToWin))//todo check why works only with tolist
        val filtered = dividedLanes.filter(_.size == connectedToWin)
        val result = filtered.map(laneResult(state)).find(_.isDefined).flatten
        if (result.isEmpty && isFull(state))
          Some(Draw)
        else
          result map (Winner(_))
      }

      @tailrec
      private def divideIn(lane: Seq[Coordinate], accumulator: Seq[Seq[Coordinate]])(divisor: Int): Seq[Seq[Coordinate]] = lane match {
        case head :: tl if tl.size >= divisor - 1 => divideIn(tl, Seq(head :: tl.take(divisor-1)) ++: accumulator)(divisor)
        case _ => accumulator
      }

      private def laneResult(state: State)(lane: Seq[Coordinate]): Option[ConnectFourPawn] = {
        val distinct = lane.map(state.board(_)).distinct
        if (distinct.size == 1) distinct.head else None
      }

      private def isFull(state: State): Boolean =
        state.board.boardMap.size == ConnectFourBoard.width * ConnectFourBoard.height
    }

  implicit lazy val turns: TurnState[ConnectFourPawn, State] = new TurnState[ConnectFourPawn, State] {
    override def turn(state: State): ConnectFourPawn =
      state.currentTurn

    override def nextTurn(state: State): State =
      state.copy(currentTurn = ConnectFourPawn.opponent(state.currentTurn))
  }

  object TicTacToeRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
    onMove matching {
      case Put(x) => state =>
        val emptyTiles = ConnectFourBoard.rows.flatten.filter(coordinate => coordinate.x == x && state.board(coordinate).isEmpty)
        val firstYEmpty = emptyTiles.foldLeft(0)((maxY, coordinate) => if (coordinate.y > maxY) coordinate.y else maxY)
        val newBoard = state.board.place(state.currentTurn, (x, firstYEmpty))
        state.setBoard(newBoard).nextTurn()
    }

    moveGeneration { implicit context =>
      iterating over emptyTiles as { t =>
        generate(Put(t.x))
      }
    }
  }
}
