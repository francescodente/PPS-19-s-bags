package sbags.interaction.view

trait Renderer[G] {
  def render(state: G): Unit
}

