package sbags.interaction.view

/**
 * A component capable of displaying part of the Game State
 * @tparam G the type of the game state.
 */
trait Renderer[G] {
  /**
   * Displays a particular information about the given game state.
   * @param state the game state whose information will be displayed.
   */
  def render(state: G): Unit
}

