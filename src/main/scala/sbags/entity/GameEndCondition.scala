package sbags.entity

/**
 * Defines a concept that decides, after making a Move, if the game should end and in that case what the result should be.
 * @tparam R the type of the Result of the game.
 */
trait GameEndCondition[R] extends GameState {
  /**
   * Returns an Optional containing a Result if any, None otherwise.
   * @return an Optional containing a Result if any, None otherwise.
   */
  def gameResult: Option[R]
  override def executeMove(move: Move): Unit =
    if (gameResult.isEmpty) super.executeMove(move)
}