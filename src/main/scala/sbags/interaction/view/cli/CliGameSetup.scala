package sbags.interaction.view.cli

import sbags.interaction.controller.ApplicationController
import sbags.interaction.view.{Quit, RendererBuilder}
import sbags.interaction.{GameSetup, RenderingSetup}
import sbags.model.core.{GameDescription, RectangularStructure}
import sbags.model.extension.{BoardState, GameEndCondition, TurnState}

abstract class CliGameSetup[M, B <: RectangularStructure, G](gameDescription: GameDescription[M, G])
  extends GameSetup[M]
    with RenderingSetup[G, CliRenderer[G]] {
  private val defaultParserConfiguration = new InputParserBuilder()
    .addKeyword("quit", Quit)

  def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder
  def inputParser: InputParser = setupInputParser(defaultParserConfiguration).parser

  def coordinateConverters: (Converter[Int], Converter[Int]) = (Converters.oneBased, Converters.oneBased)
  val separator: String = " "
  val emptyTileString: String = "_"
  def pawnToString(pawn: B#Pawn): String = pawn.toString
  def tileToString(tileContent: Option[B#Pawn]): String = tileContent match {
    case Some(p) => pawnToString(p)
    case _ => emptyTileString
  }

  implicit class RendererBuilderOps(builder: Rendering) {
    def withBoard(implicit ev: BoardState[B, G]): Rendering =
      builder.addRenderer(CliBoardRenderer(
        xModifier = coordinateConverters._1.toString,
        yModifier = coordinateConverters._2.toString,
        separator = separator,
        tileToString = tileToString
      ))

    def withTurns[T](implicit ev: TurnState[T, G]): Rendering =
      builder.addRenderer(new CliTurnRenderer)

    def withGameResult[R](implicit ev: GameEndCondition[R, G]): Rendering =
      builder.addRenderer(new CliGameResultRenderer)
  }

  private val renderers = setupRenderers(RendererBuilder()).renderers
  private val stateToGameView = CliGameView[G](renderers, inputParser, _)
  private val view = new CliView(stateToGameView)
  new ApplicationController[M, G](gameDescription, view, this).start()
}
