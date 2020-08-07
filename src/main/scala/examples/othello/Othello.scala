package examples.othello

import sbags.core.extension.BoardState
import sbags.core.{Board, GameDescription}
import sbags.core.ruleset.RuleSet

object Othello extends GameDescription[OthelloMove, OthelloState] {
  type BoardStructure = OthelloBoard.type

  def initialBoard: Board[BoardStructure] = Board(OthelloBoard)
    .place(White, (3, 3))
    .place(White, (4, 4))
    .place(Black, (3, 4))
    .place(Black, (4, 3))

  override protected def initialState: OthelloState = OthelloState(initialBoard, Black)

  override val ruleSet: RuleSet[OthelloMove, OthelloState] = OthelloRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((g, b) => g.copy(board = b))
}
