package sbags.interaction.view

import sbags.core.{BoardGameState, RectangularBoardStructure}

class CliView(xModifier: Int => String, yModifier: Int => String) extends BasicView {

  type Board <: RectangularBoardStructure
  type State <: BoardGameState[Board]
  private val stringifier = Stringifier[State, Board](xModifier, yModifier)

  override def moveAccepted(gameState: State): Unit =
    println(stringifier.buildBoard(gameState.boardState))

  override def moveRejected(): Unit = println("last move was illegal")
}

object CliView {
  def apply(xyModifier: Int => String = _ + 1 + ""): CliView = apply(xyModifier, xyModifier)

  def apply(xModifier: Int => String, yModifier: Int => String): CliView = new CliView(xModifier, yModifier)
}
