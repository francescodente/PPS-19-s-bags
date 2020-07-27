package sbags.core.extension

object Results {
  trait WinOrDraw[+P]
  case class Winner[+P](player: P) extends WinOrDraw[P]
  case object Draw extends WinOrDraw[Nothing]
}