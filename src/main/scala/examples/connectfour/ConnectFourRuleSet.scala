package examples.connectfour

import examples.connectfour.ConnectFour._
import sbags.model.core.{Coordinate, RuleSet}
import sbags.model.dsl.{Feature, RuleSetBuilder}
import sbags.model.extension._

object ConnectFourRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {

  /**
   * Returns the first empty tile in a given column.
   * @param x column.
   * @return the first with no Pawn on it.
   */
  def firstEmptyTile(x: Int): Feature[State, Coordinate] =
    col(x) map ((s, ts) => ts.filter(s.boardState(_).isEmpty).maxBy(_.y))

  onMove matching {
    /** When the Put move is performed place the current player (currentTurn) on the first empty tile of the target column (x). */
    case Put(x) =>
      > place currentTurn on firstEmptyTile(x)
  }

  after eachMove changeTurn

  moveGeneration {
    /** Legal moves are Put on each of the tiles with no pawn on them. */
    iterating over row(0) as { t =>
      when (t is empty) {
        generate(Put(t.x))
      }
    }
  }
}
