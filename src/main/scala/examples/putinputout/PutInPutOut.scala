package examples.putinputout

import sbags.entity.{BasicBoard, BasicGameState, BoardGameDescription, Move}

/**
 * Extends [[BoardGameDescription]] defining types relative to PutInPutOut game.
 */
class PutInPutOut extends BoardGameDescription {

  type BoardState = PutInPutOutBoard
  type GameState = PutInPutOutState

  override def newGame: PutInPutOutState = new PutInPutOutState(new PutInPutOutBoard)
}

/**
 * Represent the only Tiles placeable in [[examples.putinputout.PutInPutOutBoard]].
 */
sealed trait PutInPutOutTile
/**
 * Extends [[examples.putinputout.PutInPutOutTile]].
 * Represents the specific Tile placeable in [[examples.putinputout.PutInPutOutBoard]].
 */
case object TheTile extends PutInPutOutTile

/**
 * Represent the only Pawns playable in [[examples.putinputout.PutInPutOutBoard]].
 */
sealed trait PutInPutOutPawn
/**
 * Extends [[PutInPutOutPawn]].
 * Represents the specific Pawn playable in [[examples.putinputout.PutInPutOutBoard]].
 */
case object ThePawn extends PutInPutOutPawn

/**
 * Extends [[sbags.entity.BasicBoard]] defining Tile as [[examples.putinputout.PutInPutOutTile]]
 * and Pawn as [[examples.putinputout.PutInPutOutPawn]].
 */
class PutInPutOutBoard extends BasicBoard {
  type Tile = PutInPutOutTile
  type Pawn = PutInPutOutPawn
}

/**
 * Extends [[BasicGameState]] defining the type of the Board as
 * [[examples.putinputout.PutInPutOutBoard]].
 *
 * @param putInPutOutBoard represents the [[examples.putinputout.PutInPutOutBoard]] of the game.
 */
class PutInPutOutState(putInPutOutBoard: PutInPutOutBoard) extends BasicGameState(putInPutOutBoard)

/**
 * Extends [[Move]] defining [[examples.putinputout.PutInPutOutState]]
 * as type of Game State.
 * It's the move that represents the placement of [[examples.putinputout.ThePawn]]
 * in [[examples.putinputout.TheTile]].
 */
case object PutIn extends Move[PutInPutOutState] {
  /**
   * Inserts [[examples.putinputout.ThePawn]] in [[examples.putinputout.TheTile]].
   * @param gameState on which the Move will be applied.
   * @return the new Game State.
   */
  override def execute(gameState: PutInPutOutState): PutInPutOutState = {
    gameState.boardState << (ThePawn -> TheTile)
    gameState
  }
}

/**
 * Extends [[Move]] defining [[examples.putinputout.PutInPutOutState]]
 * as type of Game State.
 * It's the move that represents the removal of what is placed in [[examples.putinputout.TheTile]].
 */
case object PutOut extends Move[PutInPutOutState] {

  /**
   * Removes what is placed on [[examples.putinputout.TheTile]]
   * (in [[examples.putinputout.PutInPutOut]] it can only be [[examples.putinputout.ThePawn]]).
   * @param gameState on which the Move will be applied.
   * @return the new Game State.
   */
  override def execute(gameState: PutInPutOutState): PutInPutOutState = {
    gameState.boardState <# TheTile
    gameState
  }
}
