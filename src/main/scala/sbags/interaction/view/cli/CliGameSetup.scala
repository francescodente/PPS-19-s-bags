package sbags.interaction.view.cli

import sbags.interaction.view.{Event, LaneSelected, Quit, RendererBuilder, TileSelected}
import sbags.interaction.{GameSetup, RenderingSetup}
import sbags.model.core.{BoardStructure, RectangularStructure}
import sbags.model.extension.{BoardState, GameEndCondition, TurnState}

import scala.util.matching.Regex

trait CliGameSetup[M, G] extends GameSetup[M, G] with RenderingSetup[G, CliRenderer[G]] {
  private val renderers = setupRenderers(RendererBuilder()).renderers
  private val stateToGameView = CliGameView[G](renderers, inputParser, _)

  override val view = new CliView(stateToGameView)

  private val defaultParserConfiguration = new InputParserBuilder()
    .addKeyword("quit", Quit)

  def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder
  def inputParser: String => Option[Event] = setupInputParser(defaultParserConfiguration).parser

  implicit class CliRenderingOps(builder: RendererBuilder[G, CliRenderer[G]]) {
    def withTurns[T](implicit ev: TurnState[T, G]): RendererBuilder[G, CliRenderer[G]] =
      builder.addRenderer(new CliTurnRenderer)

    def withGameResult[R](implicit ev: GameEndCondition[R, G]): RendererBuilder[G, CliRenderer[G]] =
      builder.addRenderer(new CliGameResultRenderer)
  }
}

trait BoardSetup[B <: BoardStructure, G] {
  val emptyTileString: String = "_"

  def pawnToString(pawn: B#Pawn): String = pawn.toString

  def tileToString(tileContent: Option[B#Pawn]): String = tileContent match {
    case Some(p) => pawnToString(p)
    case _ => emptyTileString
  }
}

trait RectangularBoardSetup[B <: RectangularStructure, G] extends BoardSetup[B, G] {
  val separator: String = " "

  def coordinateConverters: (Converter[Int], Converter[Int]) = (Converters.oneBased, Converters.oneBased)

  implicit class RectangularBoardCommandRules(builder: InputParserBuilder) {
    private val lane = """^(\S+)$""".r
    private val tile = """^(\S+),(\S+)$""".r

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

    def addColumnCommand(regex: Regex = lane, toEvent: Int => Event = LaneSelected): InputParserBuilder =
      addLaneCommand(coordinateConverters._1, regex, toEvent)

    def addRowCommand(regex: Regex = lane, toEvent: Int => Event = LaneSelected): InputParserBuilder =
      addLaneCommand(coordinateConverters._2, regex, toEvent)
  }

  implicit class RectangularBoardRenderingOps(builder: RendererBuilder[G, CliRenderer[G]]) {
    def withBoard(implicit ev: BoardState[B, G]): RendererBuilder[G, CliRenderer[G]] =
      builder.addRenderer(CliBoardRenderer(
        xModifier = coordinateConverters._1.toString,
        yModifier = coordinateConverters._2.toString,
        separator = separator,
        tileToString = tileToString
      ))
  }
}
