package sbags.interaction.view.cli

import sbags.core.extension.{BoardState, TurnState}
import sbags.core.{Board, GameEndCondition, RectangularBoardStructure}
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
 * @param separator the string displayed between other graphical elements.
 * @param lf line feed character.
 * @param tileToString a function mapping tiles to their representation.
 * @param ev the board game state.
 * @tparam B type of the board structure, with [[sbags.core.RectangularBoardStructure]] as an upper bound.
 * @tparam G type of the game state.
 */
class CliBoardRenderer[B <: RectangularBoardStructure, G](xModifier: Int => String,yModifier: Int => String,
                                                          separator: String, lf: String, tileToString: Option[B#Pawn] => String)
                                                         (implicit ev: BoardState[B, G]) extends CliRenderer[G] {
  import sbags.core.extension._
  override def render(state: G): Unit = print(buildBoard(state.boardState))

  private def buildBoard(board: Board[B]): String = {
    def buildRow(startingValue: String, cellValue: Int => String, finalValue: String): String = {
      startingValue +
        (0 until board.structure.width).map(x => cellValue(x)).mkString(separator, separator, separator) +
        finalValue
    }
    buildRow(separator, xModifier, lf) +
      (0 until board.structure.height).map(y =>
        buildRow(yModifier(y), x => tileToString(board(x,y)), lf)
      ).mkString("")
  }
}

object CliBoardRenderer {
  private def defaultTileToString[P](optionPawn: Option[P]): String = optionPawn match {
    case Some(pawn) => pawn.toString
    case None => "_"
  }
  private def oneBasedLane: Int => String = _ + 1 + ""

  def apply[B <: RectangularBoardStructure, G](xModifier: Int => String = oneBasedLane,
                                               yModifier: Int => String = oneBasedLane,
                                               separator: String = " ", lf: String = "\n",
                                               tileToString: Option[B#Pawn] => String = defaultTileToString _)
                                              (implicit ev: BoardState[B, G]): CliBoardRenderer[B, G] =
    new CliBoardRenderer[B, G](xModifier, yModifier, separator, lf, tileToString)
}
