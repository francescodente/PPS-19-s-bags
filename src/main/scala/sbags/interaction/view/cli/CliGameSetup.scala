package sbags.interaction.view.cli

import sbags.interaction.view.{Event, LaneSelected, Quit, RendererBuilder, TileSelected}
import sbags.interaction.{GameSetup, RenderingSetup}
import sbags.model.core.{BoardStructure, RectangularStructure}
import sbags.model.extension.{BoardState, GameEndCondition, TurnState}

import scala.util.matching.Regex

/**
 * Defines everything needed to start a new game using a [[sbags.interaction.view.cli.CliView]].
 *
 * @tparam M type of move playable in the game.
 * @tparam G type of game state.
 */
trait CliGameSetup[M, G] extends GameSetup[M, G] with RenderingSetup[G, CliRenderer[G]] {
  private def renderers = setupRenderers(RendererBuilder()).renderers
  private def stateToGameView(state: G) = CliGameView[G](renderers, inputParser, state)

  override val view = new CliView(stateToGameView)

  private val defaultParserConfiguration = new InputParserBuilder()
    .addKeyword("quit", Quit)

  /**
   * updates the [[sbags.interaction.view.cli.InputParserBuilder]] if needed.
   *
   * @param builder the parser to updated.
   * @return the new parser updated.
   */
  def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder

  /** Returns the function that convert each String into Option of [[sbags.interaction.view.Event]]s. */
  def inputParser: String => Option[Event] = setupInputParser(defaultParserConfiguration).parser

  /**
   * Improve readability of code using the OO method call:
   * builder.method(params) instead method(builder, params).
   * Adds some commands.
   *
   * @param builder the [[sbags.interaction.view.cli.InputParserBuilder]] used to invoke method.
   */
  implicit class CliRenderingOps(builder: RendererBuilder[G, CliRenderer[G]]) {
    /**
     * Adds the a [[sbags.interaction.view.cli.CliTurnRenderer]] to the builder.
     *
     * @param ev implicit param needed to know T.
     * @tparam T type of turn.
     * @return the builder with the new renderer.
     */
    def withTurns[T](implicit ev: TurnState[T, G]): RendererBuilder[G, CliRenderer[G]] =
      builder.addRenderer(new CliTurnRenderer)

    /**
     * Adds the a [[sbags.interaction.view.cli.CliGameResultRenderer]] to the builder.
     *
     * @param ev implicit param needed to know R.
     * @tparam R type of result.
     * @return the builder with the new renderer.
     */
    def withGameResult[R](implicit ev: GameEndCondition[R, G]): RendererBuilder[G, CliRenderer[G]] =
      builder.addRenderer(new CliGameResultRenderer)
  }
}

/**
 * Defines method useful in description of game with [[sbags.model.core.BoardStructure]].
 *
 * @tparam B type of boardStructure.
 * @tparam G type of game state.
 */
trait BoardSetup[B <: BoardStructure, G] {
  /** Defines an overridable String representing an empty tile. */
  val emptyTileString: String = "_"

  /** Defines an overridable function representing how render a pawn. */
  def pawnToString(pawn: B#Pawn): String = pawn.toString

  /** Defines an overridable function representing how render a tile. */
  def tileToString(tileContent: Option[B#Pawn]): String = tileContent match {
    case Some(p) => pawnToString(p)
    case _ => emptyTileString
  }
}

/**
 * Defines method useful in description of game with [[sbags.model.core.RectangularStructure]].
 *
 * @tparam B type of boardStructure.
 * @tparam G type of game state.
 */
trait RectangularBoardSetup[B <: RectangularStructure, G] extends BoardSetup[B, G] {
  /** Defines an overridable String representing a separator. */
  val separator: String = " "

  /** Defines an overridable function that is used to decide how represent tile coordinate. */
  def coordinateConverters: (Converter[Int], Converter[Int]) = (Converter.oneBased, Converter.oneBased)

  /**
   * Improve readability of code using the OO method call:
   * builder.method(params) instead method(builder, params).
   * Adds some default Commands.
   *
   * @param builder the [[sbags.interaction.view.cli.InputParserBuilder]] used to invoke method.
   */
  implicit class RectangularBoardCommandRules(builder: InputParserBuilder) {
    private val lane = """^(\S+)$""".r
    private val tile = """^(\S+),(\S+)$""".r

    /**
     * Adds default parsing rule relative to tiles.
     *
     * @return a new InputParserBuilder with the new rule.
     */
    def addTileCommand(): InputParserBuilder = {
      val xParser = coordinateConverters._1.fromString
      val yParser = coordinateConverters._2.fromString
      builder.addRule {
        case tile(x, y) if xParser.isDefinedAt(x) && yParser.isDefinedAt(y) =>
          TileSelected(xParser(x), yParser(y))
      }
    }

    private def addLaneCommand(converter: Converter[Int], regex: Regex, toEvent: Int => Event): InputParserBuilder =
      builder.addRule {
        case regex(x) if converter.fromString.isDefinedAt(x) =>
          toEvent(converter.fromString(x))
      }

    /**
     * Adds default parsing rule relative to columns.
     *
     * @param regex the regular expression that have to match to trigger the function toEvent.
     * @param toEvent the function that defines what event is triggered from the input.
     * @return a new InputParserBuilder with the new rule.
     */
    def addColumnCommand(regex: Regex = lane, toEvent: Int => Event = LaneSelected): InputParserBuilder =
      addLaneCommand(coordinateConverters._1, regex, toEvent)

    /**
     * Adds default parsing rule relative to rows.
     *
     * @param regex the regular expression that have to match to trigger the function toEvent.
     * @param toEvent the function that defines what event is triggered from the input.
     * @return a new InputParserBuilder with the new rule.
     */
    def addRowCommand(regex: Regex = lane, toEvent: Int => Event = LaneSelected): InputParserBuilder =
      addLaneCommand(coordinateConverters._2, regex, toEvent)
  }

  /**
   * Improve readability of code using the OO method call:
   * builder.method(params) instead method(builder, params).
   * Adds withBoard method.
   *
   * @param builder the [[sbags.interaction.view.RendererBuilder]] used to invoke method.
   */
  implicit class RectangularBoardRenderingOps(builder: RendererBuilder[G, CliRenderer[G]]) {
    /**
     * Updates the [[sbags.interaction.view.RendererBuilder]] adding a boardRenderer
     * using overridable parameter of [[sbags.interaction.view.cli.RectangularBoardSetup]].
     *
     * @param ev implicit parameter needed from BoardRenderer.
     * @return a new RendererBuilder with the new renderer.
     */
    def withBoard(implicit ev: BoardState[B, G]): RendererBuilder[G, CliRenderer[G]] =
      builder.addRenderer(CliBoardRenderer(
        xModifier = coordinateConverters._1.toString,
        yModifier = coordinateConverters._2.toString,
        separator = separator,
        tileToString = tileToString
      ))
  }
}
