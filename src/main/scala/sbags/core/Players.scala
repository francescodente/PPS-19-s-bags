package sbags.core

/**
 * Definition of the players the Game comprises of.
 * @tparam P type of a single player.
 */
trait Players[P] extends GameState {
  def players: Set[P]
}

/**
 * Defines the players of the Game and provides the ability of adding and removing players to the game.
 * @tparam P type of a single player.
 */
trait ModifiablePlayers[P] extends Players[P] {
  /**
   * The starting set of players.
   */
  val initialPlayers: Set[P]

  /**
   * The current set of players.
   */
  protected var playerSet: Set[P] = initialPlayers

  /**
   * Adds a player to the player set.
   * @param player player to be added.
   */
  def addPlayer(player: P): Unit = playerSet = playerSet + player

  /**
   * Removes a player from the player set.
   * @param player player to be removed.
   */
  def removePlayer(player: P): Unit = playerSet = playerSet - player

  /**
   * The current set of players.
   * @return
   */
  override def players: Set[P] = playerSet
}
