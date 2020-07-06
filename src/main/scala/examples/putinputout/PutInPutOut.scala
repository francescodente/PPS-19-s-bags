package examples.putinputout

import sbags.control.{BasicGameState, BoardGameDescription, Move}
import sbags.entity.BasicBoard

/**
 * Extends [[sbags.control.BoardGameDescription]] defining types relative to PutInPutOut game.
 */
class PutInPutOut extends BoardGameDescription {

  type BoardState = PutInPutOutBoard
  type GameState = PutInPutOutState

  override def newGame: PutInPutOutState = new PutInPutOutState(new PutInPutOutBoard)
}

/**
 * Represent the only Tiles placeable in [[PutInPutOutBoard]].
 */
sealed trait PutInPutOutTile
/**
 * Extends [[PutInPutOutTile]].
 * Represents the specific Tile placeable in [[PutInPutOutBoard]].
 */
case object TheTile extends PutInPutOutTile

/**
 * Represent the only Pawns playable in [[PutInPutOutBoard]].
 */
sealed trait PutInPutOutPawn
/**
 * Extends [[PutInPutOutPawn]].
 * Represents the specific Pawn playable in [[PutInPutOutBoard]].
 */
case object ThePawn extends PutInPutOutPawn

/**
 * Extends [[sbags.entity.BasicBoard]] defining Tile as [[PutInPutOutTile]]
 * and Pawn as [[PutInPutOutPawn]].
 */
class PutInPutOutBoard extends BasicBoard {
  type Tile = PutInPutOutTile
  type Pawn = PutInPutOutPawn
}

/**
 * Extends [[sbags.control.BasicGameState]] defining the type of the Board as [[PutInPutOutBoard]].
 * @param putInPutOutBoard represents the [[PutInPutOutBoard]] of the game.
 */
class PutInPutOutState(putInPutOutBoard: PutInPutOutBoard) extends BasicGameState(putInPutOutBoard)

/**
 * Extends [[sbags.control.Move]] defining [[PutInPutOutState]] as type of Game State.
 * It's the move that represents the placement of [[ThePawn]] in [[TheTile]].
 */
case object PutIn extends Move[PutInPutOutState] {
  /**
   * Inserts [[ThePawn]] in [[TheTile]].
   * @param gameState on which the Move will be applied.
   * @return the new Game State.
   */
  override def execute(gameState: PutInPutOutState): PutInPutOutState = {
    gameState.boardState << (ThePawn -> TheTile)
    gameState
  }
}

/**
 * Extends [[sbags.control.Move]] defining [[PutInPutOutState]] as type of Game State.
 * It's the move that represents the removal of [[ThePawn]] in [[TheTile]].
 */
case object PutOut extends Move[PutInPutOutState] {

  /**
   * Removes what is placed on [[TheTile]] (in [[PutInPutOut]] it can only be [[ThePawn]]).
   * @param gameState on which the Move will be applied.
   * @return the new Game State.
   */
  override def execute(gameState: PutInPutOutState): PutInPutOutState = {
    gameState.boardState <# TheTile
    gameState
  }
}
