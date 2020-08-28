object ConnectFourRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  def firstEmptyTile(x: Int): Feature[State, Coordinate] =
    col(x) map ((s, ts) => ts.filter(s.boardState(_).isEmpty).maxBy(_.y))

  onMove matching {
    case Put(x) =>
      > place activePlayer on firstEmptyTile(x)
  }

  after eachMove changeTurn

  moveGeneration {
    iterating over row(0) as { t =>
      when (t is empty) {
        generate (Put(t.x))
      }
    }
  }
}
