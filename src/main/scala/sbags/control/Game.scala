package sbags.control

import sbags.entity.Board

trait Game {
  type Tile
  type Pawn
  type GameBoard <: Board {
    type Tile = Game.this.Tile
    type Pawn = Game.this.Pawn
  }

  type State <: GameState {
    type GameBoard = Game.this.GameBoard
  }

  type GameMove <: Move {
    type State = Game.this.State
  }

  def executeMove(move: GameMove, state: State): State
}
