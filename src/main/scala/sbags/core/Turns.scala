package sbags.core

/**
 * Represents part of the Game Flow, namely the succession of game turns as defined by the user.
 * @tparam T the type of the Turn.
 */
trait Turns[T] extends GameState {

  /**
   * Returns an Optional containing the current turn if any, None otherwise.
   * @return an Optional containing the current turn if any, None otherwise.
   */
  def turn: T
}
