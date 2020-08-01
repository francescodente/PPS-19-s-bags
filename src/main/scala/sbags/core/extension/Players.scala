package sbags.core.extension

import scala.annotation.tailrec

trait Players[P, G] {
  def players(state: G): Seq[P]
}

trait PlayersAsTurns[P, G] extends Players[P, G] with TurnState[P, G]

object PlayersAsTurns {
  abstract class AbstractPlayersAsTurns[P, G](playersSeq: G => Seq[P], stateToPlayer: G => P) extends PlayersAsTurns[P, G] {
    override def turn(state: G): P = stateToPlayer(state)
    override def players(state: G): Seq[P] = playersSeq(state)
  }

  def roundRobin[P, G <: {def currentPlayer: P}](playersSeq: G => Seq[P], toNextState: (G,P) => G): PlayersAsTurns[P, G] =
    new AbstractPlayersAsTurns[P, G](playersSeq, _.currentPlayer) {
      override def nextTurn(state: G): G = toNextState(state, nextPlayer(players(state), state.currentPlayer))

      private def nextPlayer(playersSeq: Seq[P], currentPlayer: P): P = {
        @tailrec
        def nextPlayerTR(playersSeq: Seq[P], currentPlayer: P)(head: P): P = playersSeq match {
          case _ +: Nil => head
          case `currentPlayer` +: t => t.head
          case _ +: t => nextPlayerTR(t, currentPlayer)(head)
        }
        nextPlayerTR(playersSeq, currentPlayer)(playersSeq.head)
      }
    }

  def roundRobin[P, G <: {def players: Seq[P]}](toNextState: (G,Seq[P]) => G): PlayersAsTurns[P, G] =
    roundRobin(_.players, _.players.head, toNextState)

  def roundRobin[P, G](playersSeq: G => Seq[P], stateToPlayer: G => P, toNextState: (G,Seq[P]) => G): PlayersAsTurns[P, G] =
    new AbstractPlayersAsTurns[P, G](playersSeq, stateToPlayer) {
      override def nextTurn(state: G): G = toNextState(state, cyclePlayers(players(state)))

      private def cyclePlayers(playersSeq: Seq[P]): Seq[P] = playersSeq match {
        case h :: t => t :+ h
      }
    }
}
