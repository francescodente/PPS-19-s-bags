package examples.putinputout

import sbags.entity.{BasicBoard, BasicGameState, GameDescription, RuleSet}

/**
 * Defines types relative to PutInPutOut game and implements the newGame to create a game.
 */
object PutInPutOut extends GameDescription {
  type GameState = PutInPutOutState
  type Move = PutInPutOutMove

  override def newGame: GameState = new PutInPutOutState(new PutInPutOutBoard)
}

/**
 * Represent the only Tiles placeable in [[examples.putinputout.PutInPutOutBoard]].
 */
sealed trait PutInPutOutTile
/**
 * Represents the specific Tile placeable in [[examples.putinputout.PutInPutOutBoard]].
 */
case object TheTile extends PutInPutOutTile

/**
 * Represent the only Pawns playable in [[examples.putinputout.PutInPutOutBoard]].
 */
sealed trait PutInPutOutPawn
/**
 * Represents the specific Pawn playable in [[examples.putinputout.PutInPutOutBoard]].
 */
case object ThePawn extends PutInPutOutPawn

/**
 * Defines Tile as [[examples.putinputout.PutInPutOutTile]] and Pawn as [[examples.putinputout.PutInPutOutPawn]].
 */
class PutInPutOutBoard extends BasicBoard {
  type Tile = PutInPutOutTile
  type Pawn = PutInPutOutPawn

  override def tiles: Seq[PutInPutOutTile] = List(TheTile)
}

/**
 * Defines the type of the Board as [[examples.putinputout.PutInPutOutBoard]].
 *
 * @param putInPutOutBoard represents the [[examples.putinputout.PutInPutOutBoard]] of the game.
 */
class PutInPutOutState(putInPutOutBoard: PutInPutOutBoard) extends BasicGameState(putInPutOutBoard) {
  override type Move = PutInPutOutMove

  val ruleSet: RuleSet[PutInPutOutMove, PutInPutOutState] = new PutInPutOutRuleSet()
}

class PutInPutOutRuleSet extends RuleSet[PutInPutOutMove, PutInPutOutState] {
  override def availableMoves(implicit state: PutInPutOutState): Seq[PutInPutOutMove] = {
    state.boardState(TheTile) match {
      case Some(_) => Seq(PutOut)
      case _ => Seq(PutIn)
    }
  }

  override def executeMove(move: PutInPutOutMove)(implicit state: PutInPutOutState): Unit = move match {
    case PutIn => state.boardState << (ThePawn -> TheTile)
    case PutOut => state.boardState <# TheTile
  }
}

/**
 * Represents the type of moves available in the whole game
 */
sealed trait PutInPutOutMove

/**
 * Represents the Move that inserts [[examples.putinputout.ThePawn]] in [[examples.putinputout.TheTile]]
 */
case object PutIn extends PutInPutOutMove

/**
 * Represents the Move that removes what is placed in [[examples.putinputout.TheTile]]
 * (in [[examples.putinputout.PutInPutOut]] game, the only Pawn available is [[examples.putinputout.ThePawn]])
 */
case object PutOut extends PutInPutOutMove
