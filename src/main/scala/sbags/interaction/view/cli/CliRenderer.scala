package sbags.interaction.view.cli


import sbags.interaction.view.Renderer
import sbags.model.core.{Board, RectangularStructure}
import sbags.model.extension._

/**
 * A [[sbags.interaction.view.Renderer]] for the [[sbags.interaction.view.cli.CliGameView]].
 *
 * @tparam G type of the game state.
 */
trait CliRenderer[G] extends Renderer[G]

/**
 * Renders the current turn.
 *
 * @param turns the turns relative to the current game state.
 * @tparam T type of the turn. (used for implicit conversion)
 * @tparam G type of the game state.
 */
class CliTurnRenderer[T, G](implicit turns: TurnState[T,G]) extends CliRenderer[G] {
  override def render(state: G): Unit = println("current turn: " + state.turn)
}

/**
 * Renders the game result.
 *
 * @param gameEnd the game ending information about the current game state.
 * @tparam R type of the result. (used for implicit conversion)
 * @tparam G type of the game state.
 */
class CliGameResultRenderer[R, G](implicit gameEnd: GameEndCondition[R, G]) extends CliRenderer[G] {
  override def render(state: G): Unit = {
    val result = state.gameResult
    if (result.isDefined) println("game result: " + result.get + "\ngame ended: u may exit")
  }
}

/**
 * Renders the game board.
 *
 * @param xModifier a function mapping columns numbers to their representation.
 * @param yModifier a function mapping rows numbers to their representation.
 * @param separator the string displayed between other graphical elements.
 * @param lf line feed character.
 * @param tileToString a function mapping tiles to their representation.
 * @param ev the board game state.
 * @tparam B type of the board structure, with [[sbags.model.core.RectangularStructure]] as an upper bound.
 * @tparam G type of the game state.
 */
class CliBoardRenderer[B <: RectangularStructure, G](xModifier: Int => String,
                                                     yModifier: Int => String,
                                                     separator: String,
                                                     lf: String,
                                                     tileToString: Option[B#Pawn] => String)
                                                    (implicit ev: BoardState[B, G]) extends CliRenderer[G] {
  import sbags.model.extension._
  override def render(state: G): Unit = print(buildBoard(state.boardState))

  private def buildBoard(board: Board[B]): String = {
    def buildRow(startingValue: String, cellValue: Int => String, finalValue: String): String =
      (0 until board.structure.width)
        .map(cellValue)
        .mkString(startingValue + separator, separator, separator + finalValue)

    val firstRow = buildRow(separator, xModifier, lf)
    firstRow + (0 until board.structure.height)
        .map(y => buildRow(yModifier(y), x => tileToString(board(x,y)), lf))
        .mkString("")
  }
}

/** Factory for [[sbags.interaction.view.cli.CliBoardRenderer]] instances. */
object CliBoardRenderer {
  private def defaultTileToString[P](optionPawn: Option[P]): String = optionPawn match {
    case Some(pawn) => pawn.toString
    case None => "_"
  }
  private def oneBasedLane: Int => String = _ + 1 + ""

  /**
   * Creates a CliBoardRenderer initialized with the following params.
   *
   * @param xModifier a function mapping columns numbers to their representation.
   * @param yModifier a function mapping rows numbers to their representation.
   * @param separator the string displayed between other graphical elements.
   * @param lf line feed character.
   * @param tileToString a function mapping tiles to their representation.
   * @param ev the board game state.
   * @tparam B type of the board structure, with [[sbags.model.core.RectangularStructure]] as an upper bound.
   * @tparam G type of the game state.
   * @return the new CliBoardRenderer created.
   */
  def apply[B <: RectangularStructure, G](xModifier: Int => String = oneBasedLane,
                                          yModifier: Int => String = oneBasedLane,
                                          separator: String = " ",
                                          lf: String = "\n",
                                          tileToString: Option[B#Pawn] => String = p => defaultTileToString[B#Pawn](p))
                                          (implicit ev: BoardState[B, G]): CliBoardRenderer[B, G] =
    new CliBoardRenderer[B, G](xModifier, yModifier, separator, lf, tileToString)
}
