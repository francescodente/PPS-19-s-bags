package sbags.core

trait TurnState[T, G] {
  def turn(state: G): T
  def setTurn(state: G)(turn: T): G
}

object TurnState {
  implicit class TurnStateOps[T, G](state: G)(implicit ev: TurnState[T, G]) {
    def turn: T = ev.turn(state)
    def setTurn(turn: T): G = ev.setTurn(state)(turn)
  }
}

object C {
  implicit val turnImplicit: TurnState[Int, List[String]] = new TurnState[Int, List[String]] {
    override def turn(state: List[String]): Int = state.last.length

    override def setTurn(state: List[String])(turn: Int): List[String] = state :+ turn.toString
  }
  import TurnState._
  println(List("1", "2", "3").turn)
  List("1","2","3").setTurn(4)
}