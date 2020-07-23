package examples.connectfour

import sbags.interaction.controller.{Event, LaneSelected, SequentialController}
import sbags.interaction.view.cli._


object ConnectFourMain extends App {
  import ConnectFour._
  private val renderers = Seq(CliBoardRenderer(), new CliTurnRenderer[ConnectFour.State], new CliGameResultRenderer[ConnectFour.State])

  private def connectFourMoves(events: List[Event]) = events match {
    case LaneSelected(x) :: Nil => Some(Put(x))
    case _ => None
  }
  private val view = CliView(renderers, CliEventParser())
  private val controller = new SequentialController[ConnectFour.State,ConnectFour.Move](view, ConnectFour.newGame, connectFourMoves)

  view.addListener(controller)
  controller.startGame()
}
