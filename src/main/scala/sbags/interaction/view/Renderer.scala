package sbags.interaction.view

import sbags.core.{BoardGameState, GameEndCondition, RectangularBoardStructure, TurnState}

trait Renderer[G] {
  def render(state: G): Unit
}

trait CliRender[G] extends Renderer[G]

class CliTurnRenderer[G](implicit turns: TurnState[_,G]) extends CliRender[G] {
  override def render(state: G): Unit = write("turn is:" + turns.turn(state))
}

class CliGameEndConditionRenderer[G](implicit gameEnd: GameEndCondition[_,G]) extends CliRender[G] {
  override def render(state: G): Unit = {
    val result = gameEnd.gameResult(state)
    if (result.isDefined) write("game result:" + result.get)
    else write("match still not finished")
  }
}

class CliBoardRenderer[B <: RectangularBoardStructure, G](xModifier: Int => String, yModifier: Int => String)
                                                         (implicit ev: BoardGameState[B, G]) extends CliRender[G] {
  import BoardGameState._
  private val stringifier = BoardStringifier[B](xModifier, yModifier)
  override def render(state: G): Unit = write(stringifier.buildBoard(state.boardState))
}

object CliBoardRenderer{
  def apply[B <: RectangularBoardStructure, G](xyModifier: Int => String = _ + 1 + "")
                                              (implicit ev: BoardGameState[B, G]): CliBoardRenderer[B, G] =
    apply(xyModifier, xyModifier)

  def apply[B <: RectangularBoardStructure, G](xModifier: Int => String, yModifier: Int => String)
                                              (implicit ev: BoardGameState[B, G]): CliBoardRenderer[B, G] =
    new CliBoardRenderer(xModifier, xModifier)
}
