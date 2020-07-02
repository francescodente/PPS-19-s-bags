package sbags.entity

trait Action {
  type Tile
  type Pawn

  type ActionBoard = Board {
    type Tile >: Action.this.Tile
    type Pawn >: Action.this.Pawn
  }

  def execute(board: ActionBoard): ActionBoard
}
