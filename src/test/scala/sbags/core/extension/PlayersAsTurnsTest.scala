package sbags.core.`extension`

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers}
import sbags.core.extension._

class PlayersAsTurnsTest extends FunSpec with Matchers with MockFactory {
  trait Player
  case object P1 extends Player
  case object P2 extends Player
  trait GameState
  trait GameStateWithCurrentPlayer extends GameState {def currentPlayer: Player}
  trait GameStateWithPlayers extends GameState {def players: Seq[Player]}
  private val seq = Seq(P1, P2, P1)
  private def playerSeq[G <: GameState]: G => Seq[Player] = _ => seq
  private def stateToP1[G <: GameState]: G => Player = _ => P1
  private def toSameState[G <: GameState]: (G, Player) => G = (g,_) => g
  private def toSameStateUsingSeq[G <: GameState]: (G, Seq[Player]) => G = (g, _) => g

  describe("Turns considered as players") {
    val mockState = mock[GameState]
    val abstractPlayerAsTurns = new PlayersAsTurns.AbstractPlayersAsTurns[Player, GameState](playerSeq, stateToP1) {
      override def nextTurn(state: GameState): GameState = state
    }
    it("should be able to return the actual Player") {
      abstractPlayerAsTurns.turn(mockState) should be(P1)
    }

    it ("should be able to return the player Seq") {
      abstractPlayerAsTurns.players(mockState) should contain theSameElementsAs seq
    }
    describe("in round-robin mode"){
      val currentPlayerRR = PlayersAsTurns.roundRobin[Player, GameStateWithCurrentPlayer](playerSeq, (_, p) =>
        new GameStateWithCurrentPlayer {
          override def currentPlayer: Player = p
        })

      val playersRR = PlayersAsTurns.roundRobin[Player, GameStateWithPlayers]((_, seq) =>
        new GameStateWithPlayers {
          override def players: Seq[Player] = seq
        })

      val mockWithCurrentPlayer = mock[GameStateWithCurrentPlayer]
      val mockWithPlayers = mock[GameStateWithPlayers]

      it ("should be able to return next turn"){
        val expectedPlayer = seq(1)
        (mockWithCurrentPlayer.currentPlayer _).expects().returns(seq.head).once()
        (mockWithPlayers.players _).expects().returns(seq).once()


        currentPlayerRR.nextTurn(mockWithCurrentPlayer).currentPlayer should be(expectedPlayer)
        playersRR.nextTurn(mockWithPlayers).players.head should be(expectedPlayer)
      }

    }
  }

}
