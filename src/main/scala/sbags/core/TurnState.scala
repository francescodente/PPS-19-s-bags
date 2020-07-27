package sbags.core

trait TurnState[T, G] {
  def turn(state: G): T
  def nextTurn(state: G): G
}
