package examples.connectfour

import sbags.core.Results.{Draw, WinOrDraw, Winner}
import sbags.core.BoardGameState._
import sbags.core.TurnState._
import sbags.core.{Board, BoardGameState, Coordinate, GameDescription, TurnState, WinOrDrawCondition}
import sbags.core.dsl.RuleSetBuilder
import sbags.core.ruleset.RuleSet

object ConnectFour extends GameDescription {
  val width = 7
  val height = 6
  val connectedToWin = 4

  type Move = ConnectFourMove
  type State = ConnectFourState

  override def initialState: ConnectFourState = ConnectFourState(Board(ConnectFourBoard), Red)

  override val ruleSet: RuleSet[Move, State] = TicTacToeRuleSet

  implicit val boardState: BoardGameState[ConnectFourBoard.type, ConnectFourState] =
    new BoardGameState[ConnectFourBoard.type, ConnectFourState] {
      override def boardState(state: ConnectFourState): Board[ConnectFourBoard.type] =
        state.board

      override def setBoard(state: ConnectFourState)(board: Board[ConnectFourBoard.type]): ConnectFourState =
        state.copy(board = board)
    }

  implicit val endCondition: WinOrDrawCondition[ConnectFourPawn, ConnectFourState] =
    new WinOrDrawCondition[ConnectFourPawn, ConnectFourState] {
      override def gameResult(state: ConnectFourState): Option[WinOrDraw[ConnectFourPawn]] = {
        val dividedLanes = allLanes.flatMap(l => divideIn(l.toList)(connectedToWin))
        val filtered = dividedLanes.filter(_.size == connectedToWin)
        val result = filtered.map(laneResult(state)).find(_.isDefined).flatten
        if (result.isEmpty && isFull(state))
          Some(Draw)
        else
          result map (Winner(_))
      }

      private def allLanes: Stream[Seq[Coordinate]] =
        ConnectFourBoard.rows ++ ConnectFourBoard.cols ++
          ConnectFourBoard.descendingDiagonals ++ ConnectFourBoard.ascendingDiagonals

      private def divideIn(lane: Seq[Coordinate])(divisor: Int): Seq[Seq[Coordinate]] = lane match {
        case head :: tl if tl.size >= divisor - 1 => Seq(head :: tl.take(divisor - 1)) ++: divideIn(tl)(divisor)
        case _ => Seq.empty
      }

      private def laneResult(state: ConnectFourState)(lane: Seq[Coordinate]): Option[ConnectFourPawn] = {
        val distinct = lane.map(state.board(_)).distinct
        if (distinct.size == 1) distinct.head else None
      }

      private def isFull(state: ConnectFourState): Boolean =
        state.board.boardMap.size == ConnectFourBoard.width * ConnectFourBoard.height
    }

  implicit val turns: TurnState[ConnectFourPawn, ConnectFourState] = new TurnState[ConnectFourPawn, ConnectFourState] {
    override def turn(state: ConnectFourState): ConnectFourPawn =
      state.currentTurn

    override def nextTurn(state: ConnectFourState): ConnectFourState =
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
