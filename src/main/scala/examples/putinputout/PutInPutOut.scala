package examples.putinputout

import sbags.core._
import sbags.core.BoardGameState._
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

  override def initialState: PutInPutOutState = PutInPutOutState(Board(PutInPutOutBoard))

  override val ruleSet: RuleSet[PutInPutOutMove, PutInPutOutState] = PutInPutOutRuleSet

  implicit val boardState: BoardGameState[PutInPutOutBoard.type, PutInPutOutState] =
    new BoardGameState[PutInPutOutBoard.type, PutInPutOutState] {
      override def boardState(state: PutInPutOutState): Board[PutInPutOutBoard.type] =
        state.board

      override def setBoard(state: PutInPutOutState)(board: Board[PutInPutOutBoard.type]): PutInPutOutState =
        state.copy(board = board)
    }


  /**
   * Defines the rule set of the PutInPutOut game, which allows to place ThePawn only when TheTile is empty
   * and to remove it only when TheTile is occupied.
   */
  object PutInPutOutRuleSet extends RuleSet[PutInPutOutMove, PutInPutOutState] {
    override def availableMoves(state: PutInPutOutState): Seq[PutInPutOutMove] = state.board(TheTile) match {
      case Some(_) => Seq(PutOut)
      case _ => Seq(PutIn)
    }

    override def executeMove(move: PutInPutOutMove)(state: PutInPutOutState): PutInPutOutState = move match {
      case PutIn => state.setBoard(state.board place (ThePawn, TheTile))
      case PutOut => state.setBoard(state.board clear TheTile)
    }
  }
}
