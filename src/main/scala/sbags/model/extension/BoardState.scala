package sbags.model.extension

import sbags.model.core.{Board, BoardStructure}

/**
 * Defines the state of a generic [[sbags.model.core.BoardStructure]] in a determinate game state.
 *
 * @tparam B type of the BoardStructure.
 * @tparam G type of the game state.
 */
trait BoardState[B <: BoardStructure, G] {
  /**
   * Given a state returns the relative board.
   *
   * @param state of which return the board.
   * @return the board of the given state.
   */
  def boardState(state: G): Board[B]

  /**
   * Returns a new state in which the board is set.
   *
   * @param state old state that needs to be updated.
   * @param board new board that have to be put in the state.
   * @return the new state with the new board in.
   */
  def setBoard(state: G)(board: Board[B]): G
}

/** Factory for [[sbags.model.extension.BoardState]] instances. */
object BoardState {
  /**
   * Creates a BoardState with a given stateToBoard and newState.
   *
   * @param stateToBoard the function returning the board from the state.
   * @param newState the function that updates the board in the state.
   * @tparam B type of the boardStructure.
   * @tparam G type of the game state.
   * @return the BoardState created.
   */
  def apply[B <: BoardStructure, G](stateToBoard: G => Board[B], newState: (G, Board[B]) => G): BoardState[B, G] =
    new BoardState[B, G] {
      override def boardState(state: G): Board[B] = stateToBoard(state)

      override def setBoard(state: G)(board: Board[B]): G = newState(state, board)
    }

  /**
   * Creates a BoardState with a default stateToBoard and a given newState.
   *
   * @param newState the function that updates the board in the state.
   * @tparam B type of the boardStructure.
   * @tparam G type of the game state.
   * @return the BoardState created.
   */
  def apply[B <: BoardStructure, G <: {def board: Board[B]}](newState: (G, Board[B]) => G): BoardState[B, G] =
    BoardState(_.board, newState)
}
