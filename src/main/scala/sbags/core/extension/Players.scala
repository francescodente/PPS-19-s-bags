package sbags.core.extension

trait Players[P, G] {
  def players(state: G): Seq[P]
}

trait PlayersAsTurns[P, G] extends Players[P, G] with TurnState[P, G] {
  def currentPlayer(state: G): P
  override def turn(state: G): P = currentPlayer(state)
}

object PlayersAsTurns {
  def roundRobin[P, G](playersSeq: Seq[P]): PlayersAsTurns[P, G] = new PlayersAsTurns[P, G] {
    private val repeat = Iterator.continually(playersSeq).flatten
    private var currentPlayer = repeat.next()

    override def currentPlayer(state: G): P = currentPlayer

    override def players(state: G): Seq[P] = playersSeq

    override def nextTurn(state: G): G = {
      currentPlayer = repeat.next()
      state
    }
  }
}
