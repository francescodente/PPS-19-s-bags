package sbags.interaction.view

import sbags.core.{BoardGameState, GameState, RectangularBoard}

trait View[G <: GameState] {
  def refresh(gameState: G): Unit
  def printError(): Unit
}

class CliView[B <: RectangularBoard](iModifier: Int => String = i => i+1+"", jModifier: Int => String = j => j+1+"")
  extends View[BoardGameState[B]] {

  def tileToString(board: B)(x: Int)(y:Int): String = board(x,y) match {
    case None => "_"
    case Some(pawn) => pawn.toString
  }

  private def buildBoard(boardState: B): String = {
    val separator = " "
    val lf = "\n"
    def buildRow(startingValue: String, cellValue: Int => String, finalValue: String): String = {
      startingValue +
        (0 until boardState.width).map(x => cellValue(x)).mkString(separator, separator, separator) +
          finalValue
    }
    (0 until boardState.height).map(y =>
      buildRow(iModifier(y), tileToString(boardState)(_)(y), lf)
    ).mkString("") + buildRow(separator, jModifier, lf)
  }

  override def refresh(gameState: BoardGameState[B]): Unit = {
    val boardString = buildBoard(gameState.boardState)
    println(boardString)
  }

  override def printError(): Unit = println("last move was illegal")
}

