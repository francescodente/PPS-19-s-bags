package sbags.control

trait Move {
  type State <: GameState

  def execute(state: State): State
}
