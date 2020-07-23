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
        (for (i <- r.y until Math.min(board.width, board.height))
          yield Coordinate(r.x + (i - r.y), i)).toStream)
      val bottomTriangle: Seq[Stream[Coordinate]] = col(0).drop(1).map(c =>
        (for (j <- c.x until Math.min(board.width, board.height))
          yield Coordinate(j, j - c.x)).toStream)
      (upperTriangle ++ bottomTriangle).toStream
    }
  }

  implicit class SquareBoardExtensions(board: SquareBoard) {
    def mainDiagonal: Stream[Coordinate] = Stream.tabulate(board.size)(x => (x, x))
    def secondaryDiagonal: Stream[Coordinate] = Stream.tabulate(board.size)(x => (x, board.size - x - 1))

    def diagonals: Stream[Stream[Coordinate]] = Stream(mainDiagonal, secondaryDiagonal)
  }
}
