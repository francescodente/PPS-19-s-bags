package sbags.interaction.view

trait RendererBuilder[G, R <: Renderer[G]] {
  def addRenderer(renderer: R): RendererBuilder[G, R]
  def renderers: Seq[R]
}

object RendererBuilder {
  private class DefaultRendererBuilder[G, R <: Renderer[G]](val renderers: Seq[R]) extends RendererBuilder[G, R] {
    override def addRenderer(renderer: R): RendererBuilder[G, R] = RendererBuilder(renderers :+ renderer)
  }

  def apply[G, R <: Renderer[G]](renderers: Seq[R] = Seq.empty): RendererBuilder[G, R] =
    new DefaultRendererBuilder(renderers)
}