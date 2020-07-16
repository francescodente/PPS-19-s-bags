package sbags.interaction.view

import sbags.core.{Board, BoardGameState, RectangularBoardStructure}

class Stringifier[S <: BoardGameState[B], B <: RectangularBoardStructure]
      (xModifier: Int => String,yModifier: Int => String,
       separator: String, freeTile: String, lf: String) {

  def buildBoard(board: Board[B]): String = {
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

object Stringifier {
  def apply[S <: BoardGameState[B], B <: RectangularBoardStructure]
    (xModifier: Int => String, yModifier: Int => String, separator: String = " ",
     freeTile: String = "_", lf: String = "\n"): Stringifier[S, B] =
      new Stringifier[S, B](xModifier, yModifier, separator, freeTile, lf)
}

