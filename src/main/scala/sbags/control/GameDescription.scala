package sbags.control

import sbags.{control, entity}

/**
 * Represents the definition of a generic game, providing a factory to generate the initial state.
 */
trait GameDescription {
  /**
   * Defines the type of game state for this game description.
   */
  type GameState

  /**
   * Returns a new instance of the initial game state for this game description.
   * @return a new instance of the game state.
   */
  def newGame: GameState
}

/**
 * Extends a [[GameDescription]] forcing the game state to be a subtype of [[GameState]] and adding the constraint of
 * having a [[entity.Board]] as part of the game state.
 */
trait BoardGameDescription extends GameDescription {
  /**
   * Defines the type of the board state, that must be a subtype of the [[entity.Board]] type.
   */
  type Board <: entity.Board

  /**
   * Defines the type of game state for this game description, that must be a subtype of [[control.GameState]].
   */
  type GameState <: control.GameState[Board]
}
