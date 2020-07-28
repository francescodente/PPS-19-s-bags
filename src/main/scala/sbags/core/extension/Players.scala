package sbags.core.extension

import scala.annotation.tailrec

trait Players[P, G] {
  def players(state: G): Seq[P]
}

trait PlayersAsTurns[P, G] extends Players[P, G] with TurnState[P, G] {
  def currentPlayer(state: G): P
  override def turn(state: G): P = currentPlayer(state)
}

object PlayersAsTurns {
  def roundRobin[P, G <: {def players: Seq[P]}](toNextState: (G,Seq[P]) => G): PlayersAsTurns[P, G] =
    roundRobin(_.players, _.players.head, toNextState)

  def roundRobin[P, G](playersSeq: G => Seq[P], stateToPlayer: G => P, toNextState: (G,Seq[P]) => G): PlayersAsTurns[P, G] =
    new PlayersAsTurns[P, G] {
      override def currentPlayer(state: G): P = stateToPlayer(state)
      override def players(state: G): Seq[P] = playersSeq(state)
      override def nextTurn(state: G): G = toNextState(state, nextPlayer(players(state), currentPlayer(state)))

      @tailrec
      private def nextPlayer(playersSeq: Seq[P], currentPlayer: P): Seq[P] = playersSeq match {
        case h :: t if h == currentPlayer => t ++ Seq(h)
        case h :: t => nextPlayer(t ++ Seq(h), currentPlayer)
      }
    }
}
