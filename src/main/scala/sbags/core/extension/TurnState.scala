package sbags.core.extension

trait TurnState[T, G] {
  def turn(state: G): T
  def nextTurn(state: G): G
}
