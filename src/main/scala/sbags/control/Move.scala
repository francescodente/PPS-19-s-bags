package sbags.control

trait Move[T, P] {
  def execute(state: GameState[T, P]): GameState[T, P]
}
