package sbags.control

trait Game {
  type Pawn
  type Tile
  type State <: GameState[Tile, Pawn]
  type GameMove <: Move[Tile, Pawn]

  def state: State
  def executeMove(move: GameMove): State
}
