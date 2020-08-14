package examples.othello

import sbags.model.core.RuleSet
import examples.othello.Othello._
import sbags.model.core.Coordinate
import sbags.model.dsl.RuleSetBuilder
import sbags.model.dsl.Chainables._

object OthelloRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  val valid: State => Coordinate => Boolean = g => tile =>
    raysFrom(tile).exists(tilesToBeFlipped(_)(g).nonEmpty)

  val raysFrom: Coordinate => Seq[Stream[Coordinate]] = t => for (
    dir <- directions;
    ray = Stream.iterate(t + dir)(_ + dir).takeWhile(OthelloBoard.containsTile(_))
  ) yield ray

  val directions: Seq[(Int, Int)] = for (
    x <- -1 to 1;
    y <- -1 to 1
    if (x, y) != (0, 0)
  ) yield (x, y)

  val tilesToBeFlipped: Stream[Coordinate] => State => Stream[Coordinate] = ray => state => {
    val active = state.currentPlayer
    val opposite = opponent(active)
    val (start, end) = ray span (state.board(_) contains opposite)
    if (end.headOption flatMap (state.board(_)) contains active) start else Stream.empty
  }

  def opponent(pawn: OthelloPawn): OthelloPawn = pawn match {
    case Black => White
    case White => Black
  }

  onMove matching {
    case Put(target) =>
      > place currentTurn on target and {
        iterating over raysFrom(target) as { r =>
          iterating over tilesToBeFlipped(r) as { t =>
            > replace t using opponent
          }
        }
      } and
      changeTurn and {
        when (availableMoves(_).isEmpty) {
          changeTurn
        }
      }
  }

  moveGeneration {
    iterating over emptyTiles as { t =>
      when (t is valid) {
        generate (Put(t))
      }
    }
  }
}