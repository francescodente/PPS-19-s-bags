package examples.othello

import sbags.model.extension.Results.{Draw, Winner}
import sbags.model.extension.{BoardState, PlayersAsTurns, WinOrDrawCondition}
import sbags.model.core.RuleSet
import sbags.model.core.{Board, GameDescription}

object Othello extends GameDescription[OthelloMove, OthelloState] {
  type BoardStructure = OthelloBoard.type

  private val players = Seq(Black, White)

  /** The initial board comprises of four pieces arranged in an X shape in the middle of the board. */
  def initialBoard: Board[BoardStructure] = Board(OthelloBoard)
    .place(White, (OthelloBoard.width / 2 - 1, OthelloBoard.height / 2 - 1))
    .place(White, (OthelloBoard.width / 2,     OthelloBoard.height / 2))
    .place(Black, (OthelloBoard.width / 2 - 1, OthelloBoard.height / 2))
    .place(Black, (OthelloBoard.width / 2,     OthelloBoard.height / 2 - 1))

  /** The initial state comprises of the initial board and Black as the current player. */
  override protected def initialState: OthelloState = OthelloState(initialBoard, Black)

  override val ruleSet: RuleSet[Move, State] = OthelloRuleSet

  /** Enables the BoardState extension. */
  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((g, b) => g.copy(board = b))

  /** Enables the TurnState extension. */
  implicit lazy val turns: PlayersAsTurns[OthelloPawn, State] =
    PlayersAsTurns.roundRobin(_ => players, (g, p) => g.copy(currentPlayer = p))

  /** Enables the GameEndCondition extension. */
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
