package examples.putinputout

import sbags.model.core.{Board, GameDescription}
import sbags.model.extension.BoardState
import sbags.model.ruleset.RuleSet

/**
 * Defines the PutInPutOut game, namely, a factory to create the initial state of the game.
 * The game involves a board composed by a single tile, called "TheTile", on which the user can place (i.e. PutIn)
 * or remove (i.e. PutOut) a single pawn called "ThePawn".
 * The state type for this game is [[examples.putinputout.PutInPutOutState]].
 */
object PutInPutOut extends GameDescription[PutInPutOutMove, PutInPutOutState] {
  type BoardStructure = PutInPutOutBoard.type

  override def initialState: State = PutInPutOutState(Board(PutInPutOutBoard))

  override val ruleSet: RuleSet[Move, State] = PutInPutOutRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((s, b) => s.copy(board = b))
}
