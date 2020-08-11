package sbags.model.core

trait Game[M, G] {
  def currentState: G
  def executeMove(move: M): Either[Failure, G]
}

object Game {
  def apply[M, G](initialState: G, ruleSet: RuleSet[M, G]): Game[M, G] =
    new BasicGame(initialState, ruleSet)

  class BasicGame[M, G](private var state: G, protected val ruleSet: RuleSet[M, G]) extends Game[M, G] {
    override def currentState: G = state

    override def executeMove(move: M): Either[Failure, G] = {
      if (!ruleSet.isValid(move)(state)) {
        Left(InvalidMove)
      } else try {
        state = ruleSet.executeMove(move)(state)
        Right(state)
      } catch {
        case t: Throwable => Left(Error(t))
      }
    }
  }
}

trait Failure

case object InvalidMove extends Failure
case class Error(cause: Throwable) extends Failure
