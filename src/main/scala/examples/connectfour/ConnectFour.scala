package examples.connectfour

import sbags.core.extension.Results.{Draw, WinOrDraw, Winner}
import sbags.core.{Board, Coordinate, GameDescription, WinOrDrawCondition}
import sbags.core.dsl.{Feature, RuleSetBuilder}
import sbags.core.ruleset.RuleSet
import sbags.core.extension._

import scala.annotation.tailrec

object ConnectFour extends GameDescription {
  val width = 7
  val height = 6
  val connectedToWin = 4
  private val players: Seq[ConnectFourPawn] = Seq(Red, Blue)

  type Move = ConnectFourMove
  type State = ConnectFourState

  type BoardStructure = ConnectFourBoard.type

  override def initialState: State = ConnectFourState(Board(ConnectFourBoard), Red)

  override val ruleSet: RuleSet[Move, State] = ConnectFourRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((s, b) => s.copy(board = b))

  implicit lazy val turns: PlayersAsTurns[ConnectFourPawn, State] =
    PlayersAsTurns.roundRobin(_ => players, (s, p) => s.copy(currentPlayer = p))

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

  object ConnectFourRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
    def firstEmptyTile(x: Int): Feature[State, Coordinate] =
      col(x) map ((s, ts) => ts.filter(s.boardState(_).isEmpty).maxBy(_.y))

    onMove matching {
      case Put(x) =>
        > place currentTurn on firstEmptyTile(x)
    }

    after each move -> changeTurn

    moveGeneration {
      iterating over row(0) as { t =>
        when (t is empty) {
          generate(Put(t.x))
        }
      }
    }
  }
}

