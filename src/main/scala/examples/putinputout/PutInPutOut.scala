package examples.putinputout

import sbags.core._
import sbags.core.dsl.RuleSetBuilder
import sbags.core.extension.BoardState
import sbags.core.ruleset.RuleSet

/**
 * Defines the PutInPutOut game, namely, a factory to create the initial state of the game.
 * The game involves a board composed by a single tile, called "TheTile", on which the user can place (i.e. PutIn)
 * or remove (i.e. PutOut) a single pawn called "ThePawn".
 * The state type for this game is [[examples.putinputout.PutInPutOutState]].
 */
object PutInPutOut extends GameDescription {
  type Move = PutInPutOutMove
  type State = PutInPutOutState
  type BoardStructure = PutInPutOutBoard.type

  override def initialState: State = PutInPutOutState(Board(PutInPutOutBoard))

  override val ruleSet: RuleSet[Move, State] = PutInPutOutRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, PutInPutOutState] =
    BoardState((s, b) => s.copy(board = b))

  /**
   * Defines the rule set of the PutInPutOut game, which allows to place ThePawn only when TheTile is empty
   * and to remove it only when TheTile is occupied.
   */
  object PutInPutOutRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
    onMove (PutIn) {
      > place ThePawn on TheTile
    }
    onMove (PutOut) {
      > clear TheTile
    }

    moveGeneration { implicit context =>
      when (TheTile is empty) { generate(PutIn) }
    }
    moveGeneration { implicit context =>
      when (TheTile isNot empty) { generate (PutOut) }
    }
  }
}
