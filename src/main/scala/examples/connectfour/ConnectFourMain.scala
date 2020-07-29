package examples.connectfour

import sbags.interaction.controller.{Event, LaneSelected, SequentialController}
import sbags.interaction.view.cli._
import ConnectFour._
import sbags.interaction.view.cli.{CliBoardRenderer, CliGameResultRenderer, CliTurnRenderer}

object ConnectFourMain extends App {

  private val pawnToString: Option[BoardStructure#Pawn] => String = {
    case Some(Red) => "R"
    case Some(Blue) => "B"
    case None => "_"
  }

  private val renderers = Seq(
    CliBoardRenderer[BoardStructure, State](tileToString = pawnToString),
    new CliTurnRenderer[State],
    new CliGameResultRenderer[State]
  )

  private val connectFourMoves: List[Event] => Option[Move] = {
    case LaneSelected(x) :: Nil => Some(Put(x))
    case _ => None
  }

  private val view = CliGameView(renderers, CliEventParser())
  private val controller = new SequentialController(view, newGame, connectFourMoves)

  view.addListener(controller)
  controller.startGame()
}
