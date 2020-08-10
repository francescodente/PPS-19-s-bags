package examples.connectfour

import examples.connectfour.ConnectFour._
import sbags.model.core.Coordinate
import sbags.model.dsl.{Feature, RuleSetBuilder}
import sbags.model.extension._
import sbags.model.ruleset.RuleSet

object ConnectFourRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  def firstEmptyTile(x: Int): Feature[State, Coordinate] =
    col(x) map ((s, ts) => ts.filter(s.boardState(_).isEmpty).maxBy(_.y))

  onMove matching {
    case Put(x) =>
      > place currentTurn on firstEmptyTile(x)
  }

  after each move -> changeTurn

  moveGeneration {
    iterating over row(0) as { t =>
      when (t is empty) {
        generate(Put(t.x))
      }
    }
  }
}
