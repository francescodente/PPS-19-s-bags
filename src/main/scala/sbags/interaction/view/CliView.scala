package sbags.interaction.view

import sbags.core.{Board, BoardGameState, RectangularBoard}

class CliView[B <: RectangularBoard](xModifier: Int => String = i => i+1+"", yModifier: Int => String = j => j+1+"")
  extends View[BoardGameState[B]] {

  protected val stringifier = new Stringifier[B](xModifier, yModifier)

  override def refresh(gameState: BoardGameState[B]): Unit =
    println(stringifier.buildBoard(gameState.boardState))

  override def printError(): Unit = println("last move was illegal")
}

class Stringifier[B <: RectangularBoard](xModifier: Int => String, yModifier: Int => String,
                                         separator: String = " ", freeTile: String = "_") {

  def buildBoard(board: Board[B]): String = {
    val lf = "\n"
    def buildRow(startingValue: String, cellValue: Int => String, finalValue: String): String = {
      startingValue +
        (0 until board.structure.width).map(x => cellValue(x)).mkString(separator, separator, separator) +
          finalValue
    }
    (0 until board.structure.height).map(y =>
      buildRow(yModifier(y), tileToString(board)(_)(y), lf)
    ).mkString("") + buildRow(separator, xModifier, lf)
  }

  def tileToString(board: Board[B])(x: Int)(y:Int): String = board(x,y) match {
    case None => freeTile
    case Some(pawn) => pawn.toString
  }
}
