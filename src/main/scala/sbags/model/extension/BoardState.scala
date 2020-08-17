package sbags.model.extension

import sbags.model.core.{Board, BoardStructure}

trait BoardState[B <: BoardStructure, G] {
  def boardState(state: G): Board[B]

  def setBoard(state: G)(board: Board[B]): G
}

object BoardState {
  def apply[B <: BoardStructure, G](stateToBoard: G => Board[B], newState: (G, Board[B]) => G): BoardState[B, G] =
    new BoardState[B, G] {
      override def boardState(state: G): Board[B] = stateToBoard(state)

      override def setBoard(state: G)(board: Board[B]): G = newState(state, board)
    }

  def apply[B <: BoardStructure, G <: {def board: Board[B]}](newState: (G, Board[B]) => G): BoardState[B, G] =
    BoardState(_.board, newState)
}
