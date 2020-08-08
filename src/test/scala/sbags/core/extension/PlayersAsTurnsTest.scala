package sbags.core.extension

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class PlayersAsTurnsTest extends FlatSpec with Matchers with MockFactory {
  trait Player
  case object P1 extends Player
  case object P2 extends Player
  case object P3 extends Player

  trait GameState
  trait GameStateWithCurrentPlayer extends GameState {def currentPlayer: Player}
  trait GameStateWithPlayers extends GameState {def players: Seq[Player]}

  private val seq = Seq(P1, P2, P3)
  private val mockWithCurrentPlayer = mock[GameStateWithCurrentPlayer]
  private val mockWithPlayers = mock[GameStateWithPlayers]

  def newCurrentPlayerRR: PlayersAsTurns[Player, GameStateWithCurrentPlayer] =
    PlayersAsTurns.roundRobin(_ => seq,
      (_, p) => new GameStateWithCurrentPlayer {
        override def currentPlayer: Player = p
      })

  def newPlayersRR: PlayersAsTurns[Player, GameStateWithPlayers] =
    PlayersAsTurns.roundRobin(
      (_, seq) => new GameStateWithPlayers {
        override def players: Seq[Player] = seq
      })

  behavior of "Turns considered as players in round-robin mode"

  it should "return the first turn passed" in{
    val currentPlayerRR = newCurrentPlayerRR
    val playersRR: PlayersAsTurns[Player, GameStateWithPlayers] = newPlayersRR
    val expectedPlayer = seq.head
    (mockWithCurrentPlayer.currentPlayer _).expects().returns(seq.head).once()
    (mockWithPlayers.players _).expects().returns(seq).once()

    currentPlayerRR.turn(mockWithCurrentPlayer) should be(expectedPlayer)
    playersRR.turn(mockWithPlayers) should be(expectedPlayer)
  }

  it should "be able to return the right next turn" in{
    val currentPlayerRR = newCurrentPlayerRR
    val playersRR = newPlayersRR
    val expectedPlayer = seq(1)
    (mockWithCurrentPlayer.currentPlayer _).expects().returns(seq.head).once()
    (mockWithPlayers.players _).expects().returns(seq).once()

    currentPlayerRR.nextTurn(mockWithCurrentPlayer).currentPlayer should be(expectedPlayer)
    playersRR.nextTurn(mockWithPlayers).players.head should be(expectedPlayer)
  }

  it should "be able to return first turn after last one" in {
    val currentPlayerRR = newCurrentPlayerRR
    val playersRR = newPlayersRR
    val expectedPlayer = seq.head
    (mockWithCurrentPlayer.currentPlayer _).expects().returns(seq.last).once()
    (mockWithPlayers.players _).expects().returns(seq.last +: seq.dropRight(1)).once()

    currentPlayerRR.nextTurn(mockWithCurrentPlayer).currentPlayer should be(expectedPlayer)
    playersRR.nextTurn(mockWithPlayers).players.head should be(expectedPlayer)
  }

}
