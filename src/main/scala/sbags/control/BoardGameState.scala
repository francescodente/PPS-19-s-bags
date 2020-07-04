package sbags.control

import sbags.entity.Board

/**
 * Represents the state of a particular game: it includes the Board State.
 * @tparam B defines the type of the Board State with [[sbags.entity.Board]] as upper-bound.
 */
trait BoardGameState[B <: Board] {
  /**
   * Returns the actual Board State relative to this Game State.
   * @return the actual Board State.
   */
  def boardState: B
}

/**
 * Extends [[sbags.control.BoardGameState]] making it instantiable and forcing the BoardState as val.
 * @param boardState val of actual BoardStare.
 * @tparam B defines the type of the Board State with [[sbags.entity.Board]] as upper-bound
 */
class BasicGameState[B <: Board](val boardState: B) extends BoardGameState[B]

