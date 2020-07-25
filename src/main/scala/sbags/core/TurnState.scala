package sbags.core

trait TurnState[T, G] {
  def turn(state: G): T
  def nextTurn(state: G): G
}

object TurnState {
  implicit class TurnStateOps[T, G](state: G)(implicit ev: TurnState[T, G]) {
    def turn: T = ev.turn(state)
    def nextTurn(): G = ev.nextTurn(state)
  }
}
