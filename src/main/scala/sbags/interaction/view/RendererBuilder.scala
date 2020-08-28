package sbags.interaction.view

/**
 * Aggregates various [[sbags.interaction.view.Renderer]]s and provides them as requested.
 *
 * @tparam G type of the game state.
 * @tparam R type of the Renderer, with [[sbags.interaction.view.Renderer]][G] as an upper bound.
 */
trait RendererBuilder[G, R <: Renderer[G]] {
  /**
   * Adds a [[sbags.interaction.view.Renderer]] to the builder.
   *
   * @param renderer the [[sbags.interaction.view.Renderer]] to be added.
   * @return a new [[sbags.interaction.view.RendererBuilder]] with the new renderer.
   */
  def addRenderer(renderer: R): RendererBuilder[G, R]

  /**
   * Retrieves the sequence of [[sbags.interaction.view.Renderer]]s as added to the builder.
   *
   * @return the sequence of [[sbags.interaction.view.Renderer]]s as added to the builder.
   */
  def renderers: Seq[R]
}

/** Factory for [[sbags.interaction.view.RendererBuilder]] instances. */
object RendererBuilder {

  /**
   * Constructor for [[sbags.interaction.view.RendererBuilder]].
   *
   * @param renderers starting renderers.
   * @tparam G type of the game state.
   * @tparam R type of the Renderer, with [[sbags.interaction.view.Renderer]][G] as an upper bound.
   * @return a new [[sbags.interaction.view.RendererBuilder]].
   */
  def apply[G, R <: Renderer[G]](renderers: Seq[R] = Seq.empty): RendererBuilder[G, R] =
    new DefaultRendererBuilder(renderers)

  /**
   * A [[sbags.interaction.view.RendererBuilder]] holding an internal sequence of renderers.
   *
   * @param renderers starting renderers.
   * @tparam G type of the game state.
   * @tparam R type of the Renderer, with [[sbags.interaction.view.Renderer]][G] as an upper bound.
   */
  private class DefaultRendererBuilder[G, R <: Renderer[G]](val renderers: Seq[R]) extends RendererBuilder[G, R] {
    override def addRenderer(renderer: R): RendererBuilder[G, R] = RendererBuilder(renderers :+ renderer)
  }
}
