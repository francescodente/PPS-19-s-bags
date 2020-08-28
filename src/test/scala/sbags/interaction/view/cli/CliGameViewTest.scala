package sbags.interaction.view.cli

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

class CliGameViewTest extends FlatSpec with Matchers with MockFactory {

  behavior of "A CLI view of a game"

  trait SomeGameState

  val renderer1: CliRenderer[SomeGameState] = mock[CliRenderer[SomeGameState]]
  val renderer2: CliRenderer[SomeGameState] = mock[CliRenderer[SomeGameState]]
  val renderers: Seq[CliRenderer[SomeGameState]] = Seq(renderer1, renderer2)
  val gameState: SomeGameState = mock[SomeGameState]

  it should "use its renderers to display the game" in {
    val view = CliGameView(renderers, _ => None, gameState)

    (renderer1.render _).expects(*).once()
    (renderer2.render _).expects(*).once()

    view.moveAccepted(gameState)
  }
}
