package examples.connectfour

import sbags.model.core.{Board, Coordinate, GameDescription, RuleSet}
import sbags.model.extension.Results.{Draw, WinOrDraw, Winner}
import sbags.model.extension._

import scala.annotation.tailrec

object ConnectFour extends GameDescription[ConnectFourMove, ConnectFourState] {
  type BoardStructure = ConnectFourBoard.type
  override val ruleSet: RuleSet[Move, State] = ConnectFourRuleSet
  val width = 7
  val height = 6
  val connectedToWin = 4
  private val players: Seq[BoardStructure#Pawn] = Seq(Red, Blue)

  /** The initial state comprises of an empty board and Red as the starting player. */
  override def initialState: State = ConnectFourState(Board(ConnectFourBoard), Red)

  /** Enables the BoardState extension. */
  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((s, b) => s.copy(board = b))

  /** Enables the TurnState extension. */
  implicit lazy val turns: PlayersAsTurns[BoardStructure#Pawn, State] =
    PlayersAsTurns.roundRobin(_ => players, (s, p) => s.copy(currentPlayer = p))

  /** Enables the GameEndCondition extension. */
  implicit lazy val endCondition: WinOrDrawCondition[BoardStructure#Pawn, State] =
    new WinOrDrawCondition[BoardStructure#Pawn, State] {
      override def gameResult(state: State): Option[WinOrDraw[BoardStructure#Pawn]] = {
        val winnableLanes = ConnectFourBoard.allLanes
          .flatMap(l => divideIn(l, Seq.empty)(connectedToWin))
          .filter(_.size == connectedToWin)
        val result = winnableLanes.map(laneResult(state)).find(_.isDefined).flatten
        if (result.isEmpty && state.board.isFull)
          Some(Draw)
        else
          result map (Winner(_))
      }

      @tailrec
      private def divideIn(lane: Stream[Coordinate], accumulator: Seq[Seq[Coordinate]])(divisor: Int): Seq[Seq[Coordinate]] = lane match {
        case head #:: tl if tl.size >= divisor - 1 => divideIn(tl, Seq(head #:: tl.take(divisor - 1)) ++: accumulator)(divisor)
        case _ => accumulator
      }

      private def laneResult(state: State)(lane: Seq[Coordinate]): Option[BoardStructure#Pawn] = {
        val distinct = lane.map(state.board(_)).distinct
        if (distinct.size == 1) distinct.head else None
      }
    }
}

