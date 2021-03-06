package examples.othello

import sbags.model.core.{Board, GameDescription, RuleSet}
import sbags.model.extension.Results.{Draw, Winner}
import sbags.model.extension.{BoardState, PlayersAsTurns, WinOrDrawCondition}

object Othello extends GameDescription[OthelloMove, OthelloState] {
  type BoardStructure = OthelloBoard.type
  override val ruleSet: RuleSet[Move, State] = OthelloRuleSet
  private val players = Seq(Black, White)

  override protected def initialState: OthelloState = OthelloState(initialBoard, Black)

  /** The initial board comprises of an 4 pawns arranged in an X shape in the center of the board. */
  def initialBoard: Board[BoardStructure] = Board(OthelloBoard)
    .place(White, (OthelloBoard.width / 2 - 1, OthelloBoard.height / 2 - 1))
    .place(White, (OthelloBoard.width / 2,     OthelloBoard.height / 2))
    .place(Black, (OthelloBoard.width / 2 - 1, OthelloBoard.height / 2))
    .place(Black, (OthelloBoard.width / 2,     OthelloBoard.height / 2 - 1))

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
        (whitePawns, blackPawns) match {
          case (w, b) if w > b => Some(Winner(White))
          case (w, b) if b > w => Some(Winner(Black))
          case _ => Some(Draw)
        }
      } else {
        None
      }
    }
}
