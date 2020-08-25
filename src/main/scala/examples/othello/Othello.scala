package examples.othello

import sbags.model.extension.Results.{Draw, Winner}
import sbags.model.extension.{BoardState, PlayersAsTurns, WinOrDrawCondition}
import sbags.model.core.RuleSet
import sbags.model.core.{Board, GameDescription}

object Othello extends GameDescription[OthelloMove, OthelloState] {
  type BoardStructure = OthelloBoard.type

  private val players = Seq(Black, White)

  def initialBoard: Board[BoardStructure] = Board(OthelloBoard)
    .place(White, (OthelloBoard.width / 2 - 1, OthelloBoard.height / 2 - 1))
    .place(White, (OthelloBoard.width / 2,     OthelloBoard.height / 2))
    .place(Black, (OthelloBoard.width / 2 - 1, OthelloBoard.height / 2))
    .place(Black, (OthelloBoard.width / 2,     OthelloBoard.height / 2 - 1))

  override protected def initialState: OthelloState = OthelloState(initialBoard, Black)

  override val ruleSet: RuleSet[Move, State] = OthelloRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((g, b) => g.copy(board = b))

  implicit lazy val turns: PlayersAsTurns[OthelloPawn, State] =
    PlayersAsTurns.roundRobin(_ => players, (g, p) => g.copy(currentPlayer = p))

  implicit lazy val endCondition: WinOrDrawCondition[OthelloPawn, State] =
    WinOrDrawCondition { state =>
      if (state.board.isFull || ruleSet.availableMoves(state).isEmpty) {
        val pawnsOnBoard = state.board.boardMap.values
        val whitePawns = pawnsOnBoard.count(_ == White)
        val blackPawns = pawnsOnBoard.size - whitePawns
        whitePawns compareTo blackPawns match {
          case n if n > 0 => Some(Winner(White))
          case n if n < 0 => Some(Winner(Black))
          case _ => Some(Draw)
        }
      } else {
        None
      }
    }
}
