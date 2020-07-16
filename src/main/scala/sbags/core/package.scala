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
    def row(r: Int): Stream[Coordinate] = Stream.tabulate(board.width)((r, _))
    def col(c: Int): Stream[Coordinate] = Stream.tabulate(board.height)((_, c))

    def rows: Stream[Stream[Coordinate]] = Stream.tabulate(board.height)(row)
    def cols: Stream[Stream[Coordinate]] = Stream.tabulate(board.width)(col)
  }

  implicit class SquareBoardExtensions(board: SquareBoard) {
    def mainDiagonal: Stream[Coordinate] = Stream.tabulate(board.size)(x => (x, x))
    def secondaryDiagonal: Stream[Coordinate] = Stream.tabulate(board.size)(x => (x, board.size - x - 1))

    def diagonals: Stream[Stream[Coordinate]] = Stream(mainDiagonal, secondaryDiagonal)
  }
}
