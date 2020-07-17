package sbags.interaction.view

import sbags.core.{BoardGameState, RectangularBoardStructure}

class CliView[S <: BoardGameState[B], B <: RectangularBoardStructure](xModifier: Int => String, yModifier: Int => String) extends BasicView[S] {

  private val stringifier = Stringifier[S, B](xModifier, yModifier)

  override def moveAccepted(gameState: S): Unit =
    println(stringifier.buildBoard(gameState.boardState))

  override def moveRejected(): Unit = println("last move was illegal")
}

object CliView {
  def apply[S <: BoardGameState[B], B <: RectangularBoardStructure]
      (xyModifier: Int => String = _ + 1 + ""): CliView[S, B] = apply[S, B](xyModifier, xyModifier)

  def apply[S <: BoardGameState[B], B <: RectangularBoardStructure]
      (xModifier: Int => String, yModifier: Int => String): CliView[S, B] = new CliView[S, B](xModifier, yModifier)
}
