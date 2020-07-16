package sbags.interaction.controller

import sbags.core.{Game, GameState}

trait GameController[G <: GameState, M] {
  def executeMove(move: M) : Either[InvalidMove.type, G]
}

class BasicGameController[G <: GameState, M](game: Game[G, M]) extends GameController[G, M] {
  override def executeMove(move: M): Either[InvalidMove.type, G] =
    try {
      game.executeMove(move)
      Right(game.currentState)
    } catch {
      case ex: Throwable => Left(InvalidMove)
    }
}

case object InvalidMove