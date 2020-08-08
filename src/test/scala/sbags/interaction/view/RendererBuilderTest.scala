package sbags.interaction.view

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class RendererBuilderTest extends FlatSpec with Matchers with MockFactory {
  trait GameState
  trait R1 extends Renderer[GameState]

  private def newRendererBuilder: RendererBuilder[GameState, R1] = RendererBuilder()

  behavior of "A builder of renderer"

  it should "be empty when normally initialized" in {
    newRendererBuilder.renderers should contain theSameElementsAs Seq.empty
  }

  it should "be able to be initialized not empty" in {
    val rendSeq = Seq(mock[R1], mock[R1])
    RendererBuilder[GameState, Renderer[GameState]](rendSeq).renderers should contain theSameElementsAs rendSeq
  }

  it should "be able to add a renderer" in {
    val rend = mock[R1]
    newRendererBuilder.addRenderer(rend).renderers should contain theSameElementsAs Seq(rend)
  }
}
