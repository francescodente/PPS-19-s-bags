package sbags.entity

/**
 * Represents the definition of a generic game, providing a factory to generate the initial state.
 */
trait GameDescription {
  /**
   * Defines the type of game state for this game description.
   */
  type GameState

  /**
   * Defines the type of moves allowed for this game description.
   */
  type Move

  /**
   * Returns a new instance of the initial game state for this game description.
   * @return a new instance of the game state.
   */
  def newGame: GameState

  /**
   * Executes the specified the move on the game state that is implicitly defined or passed
   * as an argument.
   * @param move the move to be executed.
   * @param state the game state on which to execute the move.
   */
  def executeMove(move: Move)(implicit state: GameState)
}

/**
 * Extends [[sbags.entity.GameDescription]] forcing the game state to be a subtype of
 * [[sbags.entity.BoardGameState]] and adding the constraint of having a [[sbags.entity.Board]]
 * as part of the game state.
 */
trait BoardGameDescription extends GameDescription {
  /**
   * Defines the type of the board state used by the GameState type, that must be a subtype of the
   * [[sbags.entity.Board]] type.
   */
  type BoardState <: Board

  /**
   * Defines the type of game state for this game description, that must be a subtype of
   * [[sbags.entity.BoardGameState]].
   */
  type GameState <: BoardGameState[BoardState]
}
