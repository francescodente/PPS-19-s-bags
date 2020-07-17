package sbags.interaction.view

import sbags.core.{Board, BoardGameState, RectangularBoardStructure}

class Stringifier[S <: BoardGameState[B], B <: RectangularBoardStructure]
      (xModifier: Int => String,yModifier: Int => String,
       separator: String, lf: String, tileToString: Option[B#Pawn] => String) {

  def buildBoard(board: Board[B]): String = {
    def buildRow(startingValue: String, cellValue: Int => String, finalValue: String): String = {
      startingValue +
        (0 until board.structure.width).map(x => cellValue(x)).mkString(separator, separator, separator) +
        finalValue
    }
    (0 until board.structure.height).map(y =>
      buildRow(yModifier(y), x => tileToString(board(x,y)), lf)
    ).mkString("") + buildRow(separator, xModifier, lf)
  }
}

object Stringifier {

  private def defaultTileToString[B <: RectangularBoardStructure](optionPawn: Option[B#Pawn]): String = optionPawn match {
    case None => "_"
    case Some(pawn) => pawn.toString
  }

  def apply[S <: BoardGameState[B], B <: RectangularBoardStructure]
    (xModifier: Int => String, yModifier: Int => String, separator: String = " ",
     lf: String = "\n", tileToString: Option[B#Pawn] => String = defaultTileToString _): Stringifier[S, B] =
      new Stringifier[S, B](xModifier, yModifier, separator, lf, tileToString)
}

