package sbags.core

trait BoardState[B <: BoardStructure, G] {
  def boardState(state: G): Board[B]
  def setBoard(state: G)(board: Board[B]): G
}
