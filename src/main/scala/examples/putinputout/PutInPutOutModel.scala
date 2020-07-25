package examples.putinputout

import sbags.core.{Board, BoardStructure}

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
case class PutInPutOutState(board: Board[PutInPutOut.BoardStructure])
