package sbags.entity

trait Players[P] { self: BoardGameState[_] =>
  def players: Set[P]
}

trait ModifiablePlayers[P] extends Players[P] { self: BoardGameState[_] =>
  val initialPlayers: Set[P]
  protected var playerSet: Set[P] = initialPlayers

  def addPlayer(player: P): Unit = playerSet = playerSet + player
  def removePlayer(player: P): Unit = playerSet = playerSet - player

  override def players: Set[P] = playerSet
}
