package sbags.core

trait TurnState[T, G] {
  def turn(state: G): T
  def setTurn(state: G)(turn: T): G
}

object TurnState {
  implicit class TurnStateOps[T, G](state: G)(implicit ev: TurnState[T, G]) {
    def turn: T = ev.turn(state)
    def setTurn(turn: T): G = ev.setTurn(state)(turn)
  }
}
