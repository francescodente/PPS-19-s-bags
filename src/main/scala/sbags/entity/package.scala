package sbags

package object entity {
  case class Coordinate(x: Int, y: Int)
  implicit def tupleToCoordinate(tuple: (Int, Int)): Coordinate = Coordinate(tuple._1, tuple._2)
}
