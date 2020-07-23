package sbags.interaction.view.cli

import sbags.core.{BoardGameState, GameEndCondition, RectangularBoardStructure, TurnState}
import sbags.interaction.view.Renderer

trait CliRenderer[G] extends Renderer[G]

class CliTurnRenderer[G](implicit turns: TurnState[_,G]) extends CliRenderer[G] {
  override def render(state: G): Unit = println("current turn: " + turns.turn(state))
}

class CliGameResultRenderer[G](implicit gameEnd: GameEndCondition[_,G]) extends CliRenderer[G] {
  override def render(state: G): Unit = {
    val result = gameEnd.gameResult(state)
    if (result.isDefined) println("game result: " + result.get)
  }
}

class CliBoardRenderer[B <: RectangularBoardStructure, G](xModifier: Int => String, yModifier: Int => String)
                                                         (implicit ev: BoardGameState[B, G]) extends CliRenderer[G] {
  import BoardGameState._
  private val stringifier = BoardStringifier[B](xModifier, yModifier)
  override def render(state: G): Unit = print(stringifier.buildBoard(state.boardState))
}

object CliBoardRenderer{
  def apply[B <: RectangularBoardStructure, G](xyModifier: Int => String = _ + 1 + "")
                                              (implicit ev: BoardGameState[B, G]): CliBoardRenderer[B, G] =
    apply(xyModifier, xyModifier)

  def apply[B <: RectangularBoardStructure, G](xModifier: Int => String, yModifier: Int => String)
                                              (implicit ev: BoardGameState[B, G]): CliBoardRenderer[B, G] =
    new CliBoardRenderer(xModifier, xModifier)
}