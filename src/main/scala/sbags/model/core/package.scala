package sbags.model

package object core {

  /**
   * Represent a coordinate in a 2D space.
   *
   * @param x the column value.
   * @param y the row value.
   */
  case class Coordinate(x: Int, y: Int) {
    /**
     * Sums the [[sbags.model.core.Coordinate]] passed as offset.
     *
     * @param offset the Coordinate to sum.
     * @return a new Coordinate representing the sum.
     */
    def +(offset: Coordinate): Coordinate = Coordinate(x + offset.x, y + offset.y)
  }

  /**
   * Used to improve readability of code implicitly casting tuple to [[sbags.model.core.Coordinate]].
   *
   * @param tuple the tuple to cast.
   * @return the [[sbags.model.core.Coordinate]] representing the tuple.
   */
  implicit def tupleToCoordinate(tuple: (Int, Int)): Coordinate = Coordinate(tuple._1, tuple._2)

  /**
   * Represents a pawn on a tile.
   *
   * @param pawn the pawn placed.
   * @param tile the tile where the pawn is placed on.
   * @tparam T type of tile.
   * @tparam P type of pawn.
   */
  case class PlacedPawn[+T, +P](pawn: P, tile: T)

  /**
   * Pimps the [[sbags.model.core.RectangularStructure]] adding some utility methods.
   *
   * @param structure the structure to pimp.
   */
  implicit class RectangularBoardExtensions(structure: RectangularStructure) {
    /**
     * Returns the [[sbags.model.core.Coordinate]] representing the rth row.
     *
     * @param r number of the row to analyze.
     * @return the [[sbags.model.core.Coordinate]] representing the rth row.
     */
    def row(r: Int): Stream[Coordinate] = Stream.tabulate(structure.width)((_, r))

    /**
     * Returns the [[sbags.model.core.Coordinate]] representing the rth column.
     *
     * @param c number of the column to analyze.
     * @return the [[sbags.model.core.Coordinate]] representing the rth column.
     */
    def col(c: Int): Stream[Coordinate] = Stream.tabulate(structure.height)((c, _))

    /**
     * Returns the [[sbags.model.core.Coordinate]] representing each row of the board.
     *
     * @return the [[sbags.model.core.Coordinate]] representing each row of the board.
     */
    def rows: Stream[Stream[Coordinate]] = Stream.tabulate(structure.height)(row)

    /**
     * Returns the [[sbags.model.core.Coordinate]] representing each column of the board.
     *
     * @return the [[sbags.model.core.Coordinate]] representing each column of the board.
     */
    def cols: Stream[Stream[Coordinate]] = Stream.tabulate(structure.width)(col)

    /**
     * Returns the [[sbags.model.core.Coordinate]] representing each descending diagonal of the board.
     *
     * @return the [[sbags.model.core.Coordinate]] representing each descending diagonal of the board.
     */
    def descendingDiagonals: Stream[Stream[Coordinate]] = {
      val upperTriangle: Seq[Stream[Coordinate]] = row(0).map(r =>
        (for (i <- r.x to structure.width; if i < structure.width && i - r.x < structure.height)
          yield Coordinate(i, i - r.x)).toStream)
      val bottomTriangle: Seq[Stream[Coordinate]] = col(0).drop(1).map(c =>
        (for (j <- c.x until Math.min(structure.width, structure.height); if c.y + j < structure.height)
          yield Coordinate(c.x + j, c.y + j)).toStream)
      (upperTriangle ++ bottomTriangle).toStream
    }

    /**
     * Returns the [[sbags.model.core.Coordinate]] representing each ascending diagonal of the board.
     *
     * @return the [[sbags.model.core.Coordinate]] representing each ascending diagonal of the board.
     */
    def ascendingDiagonals: Stream[Stream[Coordinate]] = {
      val upperTriangle: Seq[Stream[Coordinate]] = col(0).map(c =>
        (for (i <- c.y until -1 by -1; if c.x + c.y - i < structure.width)
          yield Coordinate(c.x + (c.y - i), i)).toStream)
      val lowerTriangle: Seq[Stream[Coordinate]] = row(structure.height - 1).drop(1).map(r =>
        (for (j <- 0 to r.x; if r.y - j >= 0 && r.x + j < structure.width)
          yield Coordinate(r.x + j, r.y - j)).toStream)
      (upperTriangle ++ lowerTriangle).toStream
    }

    /**
     * Returns the [[sbags.model.core.Coordinate]] representing each row, column and diagonal.
     *
     * @return the [[sbags.model.core.Coordinate]] representing each row, column and diagonal.
     */
    def allLanes: Stream[Stream[Coordinate]] = rows ++ cols ++ descendingDiagonals ++ ascendingDiagonals
  }

  /**
   * Pimps the [[sbags.model.core.Board]] adding some utility methods.
   *
   * @param board the board to pimp.
   * @tparam B the type of structure of the board with [[sbags.model.core.BoardStructure]] as upper bound.
   */
  implicit class BoardExtensions[B <: BoardStructure](board: Board[B]) {
    /**
     * Returns true if the board is full, false otherwise.
     *
     * @return true if the board is full, false otherwise.
     */
    def isFull: Boolean = board.boardMap.size == board.structure.tiles.size
  }

  /**
   * Pimps the [[sbags.model.core.SquareStructure]] adding some utility methods.
   *
   * @param structure the structure to pimp.
   */
  implicit class SquareBoardExtensions(structure: SquareStructure) {
    /**
     * Returns the [[sbags.model.core.Coordinate]] representing the longest descending diagonal of the board.
     *
     * @return the [[sbags.model.core.Coordinate]] representing the longest descending diagonal of the board.
     */
    def mainDescendingDiagonal: Stream[Coordinate] = Stream.tabulate(structure.size)(x => (x, x))

    /**
     * Returns the [[sbags.model.core.Coordinate]] representing the longest ascending diagonal of the board.
     *
     * @return the [[sbags.model.core.Coordinate]] representing the longest ascending diagonal of the board.
     */
    def mainAscendingDiagonal: Stream[Coordinate] = Stream.tabulate(structure.size)(x => (x, structure.size - x - 1))

    /**
     * Returns the [[sbags.model.core.Coordinate]] representing the longest diagonals of the board.
     *
     * @return the [[sbags.model.core.Coordinate]] representing the longest diagonals of the board.
     */
    def mainDiagonals: Stream[Stream[Coordinate]] = Stream(mainDescendingDiagonal, mainAscendingDiagonal)

    /**
     * Returns the [[sbags.model.core.Coordinate]] representing each row, column and longest diagonal.
     *
     * @return the [[sbags.model.core.Coordinate]] representing each row, column and longest diagonal.
     */
    def allMainLanes: Stream[Stream[Coordinate]] = structure.rows ++ structure.cols ++ mainDiagonals
  }
}
