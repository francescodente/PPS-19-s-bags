package sbags.interaction.view

import sbags.core.{Board, RectangularBoardStructure}

class Stringifier[B <: RectangularBoardStructure]
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

  private def defaultTileToString[P](optionPawn: Option[P]): String = optionPawn match {
    case Some(pawn) => pawn.toString
    case None => "_"
  }

  def apply[B <: RectangularBoardStructure]
    (xModifier: Int => String, yModifier: Int => String, separator: String = " ",
     lf: String = "\n", tileToString: Option[B#Pawn] => String = defaultTileToString _): Stringifier[B] =
      new Stringifier[B](xModifier, yModifier, separator, lf, tileToString)
}

