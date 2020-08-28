package sbags.model.extension

/**
 * Defines the turn of a given state.
 *
 * @tparam T type of turn.
 * @tparam G type of the game state.
 */
trait TurnState[T, G] {
  /**
   * Returns the turn of the given state.
   *
   * @param state the actual state.
   * @return the actual turn.
   */
  def turn(state: G): T

  /**
   * Returns a new state in which the turn is the next one.
   *
   * @param state the old state that needs to be updated.
   * @return the new state updated.
   */
  def nextTurn(state: G): G
}

/** Factory for [[sbags.model.extension.TurnState]] instances. */
object TurnState {
  /**
   * Creates a TurnState with a given stateToTurn and toNextState.
   *
   * @param stateToTurn the function returning the actual turn from the given state.
   * @param toNextState the function returning a new state from the given old one updating turn.
   * @tparam T type of turn.
   * @tparam G type of the game state.
   * @return the TurnState created.
   */
  def apply[T, G](stateToTurn: G => T, toNextState: G => G): TurnState[T, G] =
    new TurnState[T, G] {
      override def turn(state: G): T = stateToTurn(state)

      override def nextTurn(state: G): G = toNextState(state)
    }

  /**
   * Creates a TurnState with a default stateToTurn and a given toNextState.
   *
   * @param toNextState the function returning a new state from the given old one updating turn.
   * @tparam T type of turn.
   * @tparam G type of the game state.
   * @return the TurnState created.
   */
  def apply[T, G <: {def currentTurn: T}](toNextState: G => G): TurnState[T, G] = TurnState(_.currentTurn, toNextState)
}
