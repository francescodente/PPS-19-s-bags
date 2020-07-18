package sbags.interaction.view

import sbags.core.BoardGameState._
import sbags.core.{BoardGameState, RectangularBoardStructure}

class CliView[B <: RectangularBoardStructure, G](xModifier: Int => String, yModifier: Int => String)(implicit ev: BoardGameState[B, G])
  extends BasicView[G] {

  private val stringifier = Stringifier[B](xModifier, yModifier)

  override def moveAccepted(gameState: G): Unit =
    println(stringifier.buildBoard(gameState.boardState))

  override def moveRejected(): Unit = println("last move was illegal")
}

object CliView {
  def apply[B <: RectangularBoardStructure, G](xyModifier: Int => String = _ + 1 + "")(implicit ev: BoardGameState[B, G]): CliView[B, G] =
    apply(xyModifier, xyModifier)

  def apply[B <: RectangularBoardStructure, G](xModifier: Int => String, yModifier: Int => String)(implicit ev: BoardGameState[B, G]): CliView[B, G] =
    new CliView(xModifier, yModifier)
}
