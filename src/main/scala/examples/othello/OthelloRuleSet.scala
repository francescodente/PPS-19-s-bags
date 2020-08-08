package examples.othello

import sbags.core.ruleset.RuleSet
import examples.othello.Othello._
import sbags.core.Coordinate
import sbags.core.dsl.RuleSetBuilder

object OthelloRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  val directions: Seq[(Int, Int)] = for (
    x <- -1 to 1;
    y <- -1 to 1
    if (x, y) != (0, 0)
  ) yield (x, y)

  def calculateRays(t: Coordinate): Seq[Stream[Coordinate]] = for (
    dir <- directions;
    ray = Stream.iterate(t + dir)(_ + dir).takeWhile(OthelloBoard.containsTile(_))
  ) yield ray

  def tilesToBeFlipped(state: State)(ray: Stream[Coordinate]): Stream[Coordinate] = {
    val active = state.currentPlayer
    val opposite = active match {
      case White => Black
      case Black => White
    }
    val (start, end) = ray span (state.board(_) contains opposite)
    if (end.headOption flatMap (state.board(_)) contains active) start else Stream.empty
  }

  def valid: State => Coordinate => Boolean = g => tile =>
    calculateRays(tile).exists(tilesToBeFlipped(g)(_).nonEmpty)

  moveGeneration {
    iterating over emptyTiles as { t =>
      when (t is valid) {
        generate (Put(t))
      }
    }
  }
}
