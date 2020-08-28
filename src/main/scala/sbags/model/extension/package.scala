package sbags.model

import sbags.model.core.{Board, BoardStructure}

/** Represents the object that needs to be import to use implicit extensions utilities. */
package object extension {

  /**
   * Adds several functionalities to handle the board of a game state G.
   * In particular it allows:
   * <ul>
   *  <li>access to the board state from the state instance;</li>
   *  <li>board updates with method `setBoard`;</li>
   *  <li>board updates with method `changeBoard`.</li>
   * </ul>
   *
   * @param state the game state in which is called the method.
   * @param ev the [[sbags.model.extension.BoardState]] on which are called methods.
   * @tparam B type of BoardStructure.
   * @tparam G type of game state.
   */
  implicit class BoardGameStateOps[B <: BoardStructure, G](state: G)(implicit ev: BoardState[B, G]) {
    /** Returns the board of the state. */
    def boardState: Board[B] = ev.boardState(state)

    /**
     * Sets the given board at the state.
     *
     * @param board a board that have to be put in the state.
     * @return a new state with the new board in.
     */
    def setBoard(board: Board[B]): G = ev.setBoard(state)(board)

    /**
     * Returns the updated state where the board is changed with the modified one.
     *
     * @param modifier the updater from the old board to the new one.
     * @return the old state with the new board.
     */
    def changeBoard(modifier: Board[B] => Board[B]): G = state.setBoard(modifier(state.boardState))
  }

  /**
   * Adds several functionalities to handle turn management of a game state G.
   * In particular it allows:
   * <ul>
   *  <li>access to the current turn;</li>
   *  <li>way to update turn state.</li>
   * </ul>
   *
   * @param state the game state in which is called the method.
   * @param ev the [[sbags.model.extension.TurnState]] on which are called methods.
   * @tparam T type of turn.
   * @tparam G type of game state.
   */
  implicit class TurnStateOps[T, G](state: G)(implicit ev: TurnState[T, G]) {
    /** Returns the turn of the state. */
    def turn: T = ev.turn(state)

    /** Returns a new state in which the turn is the next one. */
    def nextTurn(): G = ev.nextTurn(state)
  }

  /**
   * Adds several functionalities to handle players of a game state G.
   *
   * @param state the game state in which is called the method.
   * @param ev the [[sbags.model.extension.Players]] on which are called methods.
   * @tparam P type of players.
   * @tparam G type of game state.
   */
  implicit class PlayersOps[P, G](state: G)(implicit ev: Players[P, G]) {
    /** Returns the sequence of the active players from the state. */
    def players: Seq[P] = ev.players(state)
  }

  /**
   * Adds several functionalities to handle turns and players of a game state G.
   *
   * @param state the game state in which is called the method.
   * @param ev the [[sbags.model.extension.PlayersAsTurns]] on which are called methods.
   * @tparam P type of players.
   * @tparam G type of game state.
   */
  implicit class PlayersAsTurnsOps[P, G](state: G)(implicit ev: PlayersAsTurns[P, G]) extends PlayersOps[P, G](state) {
    /** Returns the turn of the state. */
    def currentPlayer: P = ev.turn(state)
  }

  /**
   * Adds several functionalities to handle game end condition of a game state G.
   *
   * @param state the game state in which is called the method.
   * @param ev the [[sbags.model.extension.GameEndCondition]] on which are called methods.
   * @tparam R type of result.
   * @tparam G type of game state.
   */
  implicit class GameEndConditionOps[R, G](state: G)(implicit ev: GameEndCondition[R, G]) {
    /** Returns the Option of the result if the [[sbags.model.core.Game]] is ended, None otherwise. */
    def gameResult: Option[R] = ev.gameResult(state)
  }

}
