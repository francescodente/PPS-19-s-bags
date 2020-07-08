package sbags.entity

/**
 * Represents the state of a particular game: it includes the Board State.
 * @tparam B defines the type of the Board State with [[sbags.entity.Board]] as upper-bound.
 */
trait BoardGameState[B <: Board] {

  type Move

  /**
   * Returns the actual Board State relative to this Game State.
   * @return the actual Board State.
   */
  def boardState: B


  def executeMove(move: Move)
}

/**
 * Extends [[sbags.entity.BoardGameState]] making it instantiable and forcing the BoardState as val.
 * @param boardState val representing the BoardState.
 * @tparam B defines the type of the Board State with [[sbags.entity.Board]] as upper-bound
 */
abstract class BasicGameState[B <: Board](val boardState: B) extends BoardGameState[B]
