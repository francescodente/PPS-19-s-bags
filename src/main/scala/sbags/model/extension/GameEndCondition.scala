package sbags.model.extension

import sbags.model.extension.Results.{WinOrDraw, Winner}

/**
 * Defines the conditions under which the [[sbags.model.core.Game]] must end.
 *
 * @tparam R type of the result.
 * @tparam G type of the game state.
 */
trait GameEndCondition[R, G] {
  /**
   * Returns the result of the given state.
   *
   * @param state the actual state.
   * @return the Option of the result if the [[sbags.model.core.Game]] is ended, None otherwise.
   */
  def gameResult(state: G): Option[R]
}

/** Factory for [[sbags.model.extension.GameEndCondition]] instances. */
object GameEndCondition {
  /**
   * Creates a GameEndCondition with a given stateToResult.
   *
   * @param stateToResult the function returning the result from the state.
   * @tparam R type of the result.
   * @tparam G type of the game state.
   * @return the GameEndCondition created.
   */
  def apply[R, G](stateToResult: G => Option[R]): GameEndCondition[R, G] = stateToResult(_)
}

/**
 * Defines a [[sbags.model.extension.GameEndCondition]] in which the Result is a [[sbags.model.extension.Results.Winner]].
 *
 * @tparam P type of players.
 * @tparam G type of the game state.
 */
trait WinCondition[P, G] extends GameEndCondition[Winner[P], G]

/** Factory for [[sbags.model.extension.WinCondition]] instances. */
object WinCondition {
  /**
   * Creates a WinCondition with a given stateToResult.
   *
   * @param stateToResult the function returning the result from the state.
   * @tparam P type of players.
   * @tparam G type of the game state.
   * @return the WinCondition created.
   */
  def apply[P, G](stateToResult: G => Option[Winner[P]]): WinCondition[P, G] = stateToResult(_)
}

/**
 * Defines a [[sbags.model.extension.GameEndCondition]] in which the Result is a [[sbags.model.extension.Results.WinOrDraw]].
 *
 * @tparam P type of players.
 * @tparam G type of the game state.
 */
trait WinOrDrawCondition[P, G] extends GameEndCondition[WinOrDraw[P], G]

/** Factory for [[sbags.model.extension.WinOrDrawCondition]] instances. */
object WinOrDrawCondition {
  /**
   * Creates a WinOrDrawCondition with a given stateToResult.
   *
   * @param stateToResult the function returning the result from the state.
   * @tparam P type of players.
   * @tparam G type of the game state.
   * @return the WinOrDrawCondition created.
   */
  def apply[P, G](stateToResult: G => Option[WinOrDraw[P]]): WinOrDrawCondition[P, G] = stateToResult(_)
}
