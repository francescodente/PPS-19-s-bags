package sbags.interaction.view.cli

import sbags.core.{BoardGameState, GameEndCondition, RectangularBoardStructure, TurnState}
import sbags.interaction.view.Renderer

/**
 * A [[sbags.interaction.view.Renderer]] for the [[sbags.interaction.view.cli.CliView]].
 * @tparam G type of the game state.
 */
trait CliRenderer[G] extends Renderer[G]

/**
 * Renders the current turn.
 * @param turns the turns relative to the current game state.
 * @tparam G type of the game state.
 */
class CliTurnRenderer[G](implicit turns: TurnState[_,G]) extends CliRenderer[G] {
  override def render(state: G): Unit = println("current turn: " + turns.turn(state))
}

/**
 * Renders the game result.
 * @param gameEnd the game ending information about the current game state.
 * @tparam G type of the game state.
 */
class CliGameResultRenderer[G](implicit gameEnd: GameEndCondition[_,G]) extends CliRenderer[G] {
  override def render(state: G): Unit = {
    val result = gameEnd.gameResult(state)
    if (result.isDefined) println("game result: " + result.get)
  }
}

/**
 * Renders the game board.
 * @param xModifier a function mapping columns numbers to their representation.
 * @param yModifier a function mapping rows numbers to their representation.
 * @param ev the board game state.
 * @tparam B type of the board structure, with [[sbags.core.RectangularBoardStructure]] as am upper bound.
 * @tparam G type of the game state.
 */
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
