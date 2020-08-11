package sbags.model

import org.scalatest.{FlatSpec, Matchers}
import sbags.model.core.{Board, BoardStructure}

class BoardExtensionTest extends FlatSpec with Matchers {
  private val tilePosition: Int = 0

  object TestBoard extends BoardStructure {
    type Tile = Int
    type Pawn = String

    override def tiles: Seq[Tile] = List(tilePosition)
  }

  private def newSimpleBoard: Board[TestBoard.type] = Board(TestBoard)

  behavior of "A board with extensions"

  it should "know when it's empty" in {
    val board = newSimpleBoard
    board.isFull should be(false)
  }

  it can "know when it's full" in {
    val pawnName: String = "pawnName"
    val board = newSimpleBoard place (pawnName, tilePosition)
    board.isFull should be(true)
  }

}
