package sbags.core

trait GameState

/**
 * Represents the state of a particular game: it includes the Board State.
 *
 * @tparam B defines the type of the Board State with [[sbags.core.Board]] as upper-bound.
 */
trait BoardGameState[B <: BoardStructure] extends GameState {
  /**
   * Returns the actual Board State relative to this Game State.
   * @return the actual Board State.
   */
  def boardState: Board[B]
}

/**
 * Extends [[sbags.core.BoardGameState]] making it instantiable and forcing the BoardState as val.
 *
 * @param boardState val representing the BoardState.
 * @tparam B defines the type of the Board State with [[sbags.core.Board]] as upper-bound
 */
class BasicGameState[B <: BoardStructure](val boardState: Board[B]) extends BoardGameState[B]
