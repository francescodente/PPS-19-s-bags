package examples.connectfour

import sbags.interaction.controller.{Event, LaneSelected, SequentialController}
import sbags.interaction.view.cli._
import ConnectFour._

object ConnectFourMain extends App {

  private def pawnToString(optionPawn: Option[ConnectFourPawn]): String = optionPawn match {
    case Some(Red) => "R"
    case Some(Blue) => "B"
    case None => "_"
  }

  private val renderers = Seq(
    CliBoardRenderer[BoardStructure, State](tileToString = pawnToString),
    new CliTurnRenderer[State],
    new CliGameResultRenderer[State])

  private def connectFourMoves(events: List[Event]) = events match {
    case LaneSelected(x) :: Nil => Some(Put(x))
    case _ => None
  }
  private val view = CliView(renderers, CliEventParser())
  private val controller = new SequentialController(view, ConnectFour.newGame, connectFourMoves)

  view.addListener(controller)
  controller.startGame()
}
