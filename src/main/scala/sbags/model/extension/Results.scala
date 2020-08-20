package sbags.model.extension

/** Defines some possible Results of games. */
object Results {

  /**
   * Represents the Result in which a Player may win/lose or draw.
   * @tparam P type of players.
   */
  trait WinOrDraw[+P]

  /**
   * Represents the Result in which a Player may win/lose.
   * @param player the winning player.
   * @tparam P type of players.
   */
  case class Winner[+P](player: P) extends WinOrDraw[P]

  /** Represents the Draw as Result of a [[sbags.model.core.Game]]. */
  case object Draw extends WinOrDraw[Nothing]
}
