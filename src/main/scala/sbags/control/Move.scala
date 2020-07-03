package sbags.control

trait Move[S <: GameState[_]] {
  def execute(gameState: S): S
}
