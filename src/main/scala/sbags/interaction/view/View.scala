package sbags.interaction.view

import sbags.core.{BoardGameState, GameState, RectangularBoard}

trait View[G <: GameState] {
  def refresh(gameState: G): Unit
  def printError(): Unit
}

class CliView[B <: RectangularBoard](iModifier: Int => String = i => i+1+"", jModifier: Int => String = j => j+1+"")
  extends View[BoardGameState[B]] {

  private def buildBoard(boardState: B): String = {
    val separator = " "
    val lf = "\n"
    def buildRow(startingValue: String, cellValue: Int => String, finalValue: String) =
      startingValue + (0 until boardState.width).map(j => cellValue(j)).mkString(separator, separator, separator) + finalValue
    def buildTail(i: Int)( j:Int): String = boardState(i,j) match {
      case None => "_"
      case Some(pawn) => pawn.toString //todo need of pawnToString method but don't have Pawn type
    }

    (0 until boardState.height).map(i =>
      buildRow(iModifier(i), buildTail(i), lf)
    ).mkString("") + buildRow(separator, jModifier, lf)
  }

  override def refresh(gameState: BoardGameState[B]): Unit = {
    val boardString = buildBoard(gameState.boardState)
    println(boardString)
  }

  override def printError(): Unit = println("last move was illegal")
}

