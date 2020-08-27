package sbags.model.extension

import scala.annotation.tailrec

/**
 * Defines the players of a given game state.
 *
 * @tparam P type of players.
 * @tparam G type of the game state.
 */
trait Players[P, G] {
  /**
   * Returns the sequence of the active players from a given state.
   *
   * @param state the actual state.
   * @return the sequence of the active players.
   */
  def players(state: G): Seq[P]
}

/**
 * Represents the [[sbags.model.extension.Players]] as unit of the game flow
 * (in particular as [[sbags.model.extension.TurnState]]).
 *
 * @tparam P type of players.
 * @tparam G type of the game state.
 */
trait PlayersAsTurns[P, G] extends Players[P, G] with TurnState[P, G]

/** Factory for [[sbags.model.extension.PlayersAsTurns]] instances. */
object PlayersAsTurns {

  private abstract class AbstractPlayersAsTurns[P, G](playersSeq: G => Seq[P], stateToPlayer: G => P) extends PlayersAsTurns[P, G] {
    override def turn(state: G): P = stateToPlayer(state)

    override def players(state: G): Seq[P] = playersSeq(state)
  }

  /**
   * Creates a PlayersAsTurns with a given playersSeq and toNextState and a default stateToPlayer.
   * The players play their turn following round-robin order.
   *
   * @param playersSeq the function returning the sequence of player from the state.
   * @param toNextState the function returning the new state with the given sequence of players.
   * @tparam P type of players.
   * @tparam G type of the game state.
   * @return the PlayersAsTurns created.
   */
  def roundRobin[P, G <: {def currentPlayer: P}](playersSeq: G => Seq[P], toNextState: (G, P) => G): PlayersAsTurns[P, G] =
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

  /**
   * Creates a PlayersAsTurns with a default playersSeq, stateToPlayer and a given toNextState.
   * The players play their turn following round-robin order.
   *
   * @param toNextState the function returning the new state with the given sequence of players.
   * @tparam P type of players.
   * @tparam G type of the game state.
   * @return the PlayersAsTurns created.
   */
  def roundRobin[P, G <: {def players: Seq[P]}](toNextState: (G, Seq[P]) => G): PlayersAsTurns[P, G] =
    roundRobin(_.players, _.players.head, toNextState)


  /**
   * Creates a PlayersAsTurns with a given playersSeq, stateToPlayer and toNextState.
   * The players play their turn following round-robin order.
   *
   * @param playersSeq the function returning the sequence of player from the state.
   * @param stateToPlayer the function returning the player that have to play.
   * @param toNextState the function returning the new state with the given sequence of players.
   * @tparam P type of players.
   * @tparam G type of the game state.
   * @return the PlayersAsTurns created.
   */
  def roundRobin[P, G](playersSeq: G => Seq[P], stateToPlayer: G => P, toNextState: (G, Seq[P]) => G): PlayersAsTurns[P, G] =
    new AbstractPlayersAsTurns[P, G](playersSeq, stateToPlayer) {
      override def nextTurn(state: G): G = toNextState(state, cyclePlayers(players(state)))

      private def cyclePlayers(playersSeq: Seq[P]): Seq[P] = playersSeq match {
        case h :: t => t :+ h
      }
    }
}
