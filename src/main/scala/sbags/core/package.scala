package sbags

package object core {
  case class Coordinate(x: Int, y: Int)
  implicit def tupleToCoordinate(tuple: (Int, Int)): Coordinate = Coordinate(tuple._1, tuple._2)

  case class PlacedPawn[T, P](pawn: P, tile: T)

  object on {
    def unapply[T, P](placedPawn: PlacedPawn[T, P]): Option[(P, T)] = Some(placedPawn.pawn, placedPawn.tile)
  }

  implicit class PawnExtension[P](pawn: P) {
    def on[T](tile: T): PlacedPawn[T, P] = PlacedPawn(pawn, tile)
  }

  implicit class RectangularBoardExtensions(board: RectangularBoard) {
    def row(r: Int): Stream[Coordinate] = Stream.tabulate(board.width)((_, r))

    def col(c: Int): Stream[Coordinate] = Stream.tabulate(board.height)((c, _))

    def rows: Stream[Stream[Coordinate]] = Stream.tabulate(board.height)(row)

    def cols: Stream[Stream[Coordinate]] = Stream.tabulate(board.width)(col)

    def descendingDiagonals: Stream[Stream[Coordinate]] = {
      val upperTriangle: Seq[Stream[Coordinate]] = row(0).map(r =>
        (for (i <- r.x to board.width; if i < board.width && i - r.x < board.height)
          yield Coordinate(i, i - r.x)).toStream)
      val bottomTriangle: Seq[Stream[Coordinate]] = col(0).drop(1).map(c =>
        (for (j <- c.x until Math.min(board.width, board.height); if c.y + j < board.height)
          yield Coordinate(c.x + j, c.y + j)).toStream)
      (upperTriangle ++ bottomTriangle).toStream
    }

    def ascendingDiagonals: Stream[Stream[Coordinate]] = {
      val upperTriangle: Seq[Stream[Coordinate]] = col(0).map(c =>
        (for (i <- c.y until -1 by -1; if c.x + c.y - i < board.width) yield Coordinate(c.x + (c.y - i), i)).toStream)
      val lowerTriangle: Seq[Stream[Coordinate]] = row(board.height - 1).drop(1).map(r =>
        (for (j <- 0 to r.x; if r.y - j >= 0 && r.x + j < board.width)
          yield Coordinate(r.x + j, r.y - j)).toStream)
      (upperTriangle ++ lowerTriangle).toStream
    }
  }

  implicit class SquareBoardExtensions(board: SquareBoard) {
    def mainDescendingDiagonal: Stream[Coordinate] = Stream.tabulate(board.size)(x => (x, x))
    def mainAscendingDiagonal: Stream[Coordinate] = Stream.tabulate(board.size)(x => (x, board.size - x - 1))

    def mainDiagonals: Stream[Stream[Coordinate]] = Stream(mainDescendingDiagonal, mainAscendingDiagonal)
  }
}
