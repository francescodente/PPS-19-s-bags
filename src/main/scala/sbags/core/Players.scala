package sbags.core

trait Players[P, G] {
  def players(state: G): Seq[P]
}

object Players {
  implicit class PlayersOps[P, G](state: G)(implicit ev: Players[P, G]) {
    def players: Seq[P] = ev.players(state)
  }
}
