package sbags.interaction.view.cli

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.core.RectangularBoardStructure
import sbags.core.extension.BoardState

class CliGameGameViewTest extends FlatSpec with Matchers with MockFactory{

  behavior of "A CLI view"

  trait SomeGameState

  val renderer1: CliRenderer[SomeGameState] = mock[CliRenderer[SomeGameState]]
  val renderer2: CliRenderer[SomeGameState] = mock[CliRenderer[SomeGameState]]
  val renderers: Seq[CliRenderer[SomeGameState]] = Seq(renderer1, renderer2)
  val parser: CliEventParser = mock[CliEventParser]
  implicit val boardGameState: BoardState[RectangularBoardStructure, SomeGameState] = mock[BoardState[RectangularBoardStructure, SomeGameState]]
  val gameState: SomeGameState = mock[SomeGameState]

  it should "use its renderers to display the game" in {
    val view = CliGameView(renderers, parser)

    (renderer1.render _).expects(*).once()
    (renderer2.render _).expects(*).once()

    view.moveAccepted(gameState)
  }

}
