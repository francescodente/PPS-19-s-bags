package sbags.core

import sbags.core.Results.{WinOrDraw, Winner}

/**
 * Defines a concept that decides, after making a Move, if the game should end and in that case what the result should be.
 *
 * @tparam R the type of the Result of the game.
 */
trait GameEndCondition[R] extends GameState {
  /**
   * Returns an Optional containing a Result if any, None otherwise.
   * @return an Optional containing a Result if any, None otherwise.
   */
  def gameResult: Option[R]
}

trait WinCondition[P] extends GameEndCondition[Winner[P]] with Players[P]

trait WinOrDrawCondition[P] extends GameEndCondition[WinOrDraw[P]] with Players[P]
