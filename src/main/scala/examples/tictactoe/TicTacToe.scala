package examples.tictactoe

import sbags.model.extension._
import sbags.model.extension.Results.{Draw, WinOrDraw, Winner}
import sbags.model.core.{Board, Coordinate, GameDescription, RuleSet}

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
}
