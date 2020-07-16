package examples.putinputout

import sbags.core._
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
}

/**
 * Represents the tiles that can be found in a [[examples.putinputout.PutInPutOutBoard]].
 */
sealed trait PutInPutOutTile
/**
 * Represents the specific tile that can be found in a [[examples.putinputout.PutInPutOutBoard]].
 */
case object TheTile extends PutInPutOutTile

/**
 * Represents the pawns that can be placed in a [[examples.putinputout.PutInPutOutBoard]].
 */
sealed trait PutInPutOutPawn
/**
 * Represents the specific pawn that can be placed in a [[examples.putinputout.PutInPutOutBoard]].
 */
case object ThePawn extends PutInPutOutPawn

/**
 * Defines the board used in a PutInPutOut game, that is a board composed by a single tile, where the player
 * can place/remove a single pawn.
 * <br/>
 * Tiles are defined as [[examples.putinputout.PutInPutOutTile]] and pawns as[[examples.putinputout.PutInPutOutPawn]].
 */
object PutInPutOutBoard extends BoardStructure {
  type Tile = PutInPutOutTile
  type Pawn = PutInPutOutPawn

  override def tiles: Seq[PutInPutOutTile] = List(TheTile)
}

/**
 * Describes the state of a PutInPutOut game, which only contains the state of the board.
 * @param board the initial state of the board.
 */
case class PutInPutOutState(board: Board[PutInPutOutBoard.type]) extends BasicGameState(board)

/**
 * Defines the rule set of the PutInPutOut game, which allows to place ThePawn only when TheTile is empty
 * and to remove it only when TheTile is occupied.
 */
object PutInPutOutRuleSet extends RuleSet[PutInPutOutMove, PutInPutOutState] {
  override def availableMoves(state: PutInPutOutState): Seq[PutInPutOutMove] = state.boardState(TheTile) match {
    case Some(_) => Seq(PutOut)
    case _ => Seq(PutIn)
  }

  override def executeMove(move: PutInPutOutMove)(state: PutInPutOutState): PutInPutOutState = move match {
    case PutIn => PutInPutOutState(state.boardState place (ThePawn, TheTile))
    case PutOut => PutInPutOutState(state.boardState clear TheTile)
  }
}

/**
 * Represents the moves that can be made in the PutInPutOut game.
 */
sealed trait PutInPutOutMove

/**
 * Represents the Move that inserts [[examples.putinputout.ThePawn]] in [[examples.putinputout.TheTile]].
 */
case object PutIn extends PutInPutOutMove

/**
 * Represents the Move that removes what is placed in [[examples.putinputout.TheTile]]
 * (in [[examples.putinputout.PutInPutOut]] game, the only Pawn available is [[examples.putinputout.ThePawn]]).
 */
case object PutOut extends PutInPutOutMove
