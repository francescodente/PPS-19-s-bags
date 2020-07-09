package sbags.entity

trait GameState {
  type Move

  def ruleSet: RuleSet[Move, this.type]

  def executeMove(move: Move): Unit = {
    ruleSet.executeMove(move)(this)
  }
}

/**
 * Represents the state of a particular game: it includes the Board State.
 * @tparam B defines the type of the Board State with [[sbags.entity.Board]] as upper-bound.
 */
trait BoardGameState[B <: Board] extends GameState {
  /**
   * Returns the actual Board State relative to this Game State.
   * @return the actual Board State.
   */
  def boardState: B
}

/**
 * Extends [[sbags.entity.BoardGameState]] making it instantiable and forcing the BoardState as val.
 * @param boardState val representing the BoardState.
 * @tparam B defines the type of the Board State with [[sbags.entity.Board]] as upper-bound
 */
abstract class BasicGameState[B <: Board](val boardState: B) extends BoardGameState[B]
