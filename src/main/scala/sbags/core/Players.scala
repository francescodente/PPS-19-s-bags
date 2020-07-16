package sbags.core

/**
 * Definition of the players the Game comprises of.
 * @tparam P type of a single player.
 */
trait Players[P] extends GameState {
  def players: Set[P]
}
