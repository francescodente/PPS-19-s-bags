package sbags.core.extension

trait TurnState[T, G] {
  def turn(state: G): T
  def nextTurn(state: G): G
}

object TurnState {
  def apply[T, G](stateToTurn: G => T, toNextState: G => G): TurnState[T, G] =
    new TurnState[T, G] {
      override def turn(state: G): T = stateToTurn(state)

      override def nextTurn(state: G): G = toNextState(state)
    }

  def apply[T, G <: {def currentTurn: T}](toNextState: G => G): TurnState[T, G] = TurnState(_.currentTurn, toNextState)
}