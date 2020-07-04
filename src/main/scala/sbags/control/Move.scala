package sbags.control

trait Move[S <: BoardGameState[_]] {
  def execute(gameState: S): S
}
