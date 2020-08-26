package sbags.model.core

/**
 * Represents a single match instanced from [[sbags.model.core.GameDescription]].
 *
 * @tparam M the type of moves for this game.
 * @tparam G the type of game state.
 */
trait Game[M, G] {
  /** Returns the current state of the game. */
  def currentState: G

  /**
   * Executes the given move on the game state.
   *
   * @param move the move which have to be executed.
   * @return an [[scala.util.Either]] with a [[sbags.model.core.Failure]] in left if something went wrong,
   *         and the updated state otherwise.
   */
  def executeMove(move: M): Either[Failure, G]

  /**
   * Undoes the last move made on this game, returning an option containing the new state if there was a move
   * to be undone, or None otherwise.
   *
   * @return the optional new state.
   */
  def undoLastMove(): Option[G]
}

/** Factory for [[sbags.model.core.Game]] instances. */
object Game {
  /**
   * Creates a Game with a given initialState and ruleSet.
   *
   * @param initialState the starting state of the game.
   * @param ruleSet the rule set used to execute the moves.
   * @tparam M the type of moves for this game.
   * @tparam G the type of game state.
   * @return the created game.
   */
  def apply[M, G](initialState: G, ruleSet: RuleSet[M, G]): Game[M, G] =
    new BasicGame(initialState, ruleSet)

  private class BasicGame[M, G](initialState: G, protected val ruleSet: RuleSet[M, G]) extends Game[M, G] {
    private var history: List[G] = List(initialState)

    override def currentState: G = history.head

    override def executeMove(move: M): Either[Failure, G] =
      try {
        if (!ruleSet.isValid(move)(currentState)) {
          Left(InvalidMove)
        } else {
          val newState = ruleSet.executeMove(move)(currentState)
          history = newState :: history
          Right(newState)
        }
      } catch {
        case t: Throwable => Left(Error(t))
      }

    override def undoLastMove(): Option[G] = history.tail match {
      case h :: _ =>
        history = history.tail
        Some(h)
      case _ => None
    }
  }

}

/** Represent a fail in sbags model. */
trait Failure

/** Represent a invalid move applied on a [[sbags.model.core.Game]]. */
case object InvalidMove extends Failure

/**
 * Represent a generic error in sbags and report the cause.
 *
 * @param cause the `java.lang.Throwable` that caused the error.
 */
case class Error(cause: Throwable) extends Failure
