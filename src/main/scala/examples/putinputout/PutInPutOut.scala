package examples.putinputout

import sbags.control.{BasicGameState, BoardGameDescription, Move}
import sbags.entity.BasicBoard

class PutInPutOut extends BoardGameDescription {
  type Board = PutInPutOutBoard
  type GameState = PutInPutOutState

  override def newGame: PutInPutOutState = new PutInPutOutState(new PutInPutOutBoard)
}

sealed trait PutInPutOutTile
case object TheTile extends PutInPutOutTile
sealed trait PutInPutOutPawn
case object ThePawn extends PutInPutOutPawn

class PutInPutOutBoard extends BasicBoard {
  type Tile = PutInPutOutTile
  type Pawn = PutInPutOutPawn
}

class PutInPutOutState(putInPutOutBoard: PutInPutOutBoard) extends BasicGameState(putInPutOutBoard)

case object PutIn extends Move[PutInPutOutState] {
  override def execute(gameState: PutInPutOutState): PutInPutOutState = {
    gameState.boardState << (ThePawn -> TheTile)
    gameState
  }
}

case object PutOut extends Move[PutInPutOutState] {
  override def execute(gameState: PutInPutOutState): PutInPutOutState = {
    gameState.boardState <# TheTile
    gameState
  }
}
