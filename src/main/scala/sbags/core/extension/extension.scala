package sbags.core

package object extension {
  implicit class BoardGameStateOps[B <: BoardStructure, G](state: G)(implicit ev: BoardState[B, G]) {
    def boardState: Board[B] = ev.boardState(state)
    def setBoard(board: Board[B]): G = ev.setBoard(state)(board)
    def changeBoard(modifier: Board[B] => Board[B]): G = state.setBoard(modifier(state.boardState))
  }

  implicit class TurnStateOps[T, G](state: G)(implicit ev: TurnState[T, G]) {
    def turn: T = ev.turn(state)
    def nextTurn(): G = ev.nextTurn(state)
  }

  implicit class PlayersOps[P, G](state: G)(implicit ev: Players[P, G]) {
    def players: Seq[P] = ev.players(state)
  }

  implicit class PlayersAsTurnsOps[P, G](state: G)(implicit ev: PlayersAsTurns[P, G]) extends PlayersOps[P, G](state) {
    def currentPlayer: P = ev.currentPlayer(state)
  }

  implicit class GameEndConditionOps[R, G](state: G)(implicit ev: GameEndCondition[R, G]) {
    def gameResult: Option[R] = ev.gameResult(state)
  }
}
