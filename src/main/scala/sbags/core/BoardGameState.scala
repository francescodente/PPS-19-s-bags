package sbags.core

trait BoardGameState[B <: BoardStructure, G] {
  def boardState(state: G): Board[B]
  def setBoard(state: G)(board: Board[B]): G
}

object BoardGameState {
  implicit class BoardGameStateOps[B <: BoardStructure, G](state: G)(implicit ev: BoardGameState[B, G]) {
    def boardState: Board[B] = ev.boardState(state)
    def setBoard(board: Board[B]): G = ev.setBoard(state)(board)
  }
}
