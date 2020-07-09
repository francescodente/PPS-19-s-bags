package examples.putinputout

import sbags.entity.{BasicBoard, BasicGameState, BoardGameState, GameDescription, RuleSet}

/**
 * Extends [[sbags.entity.GameDescription]] defining types relative to PutInPutOut game.
 */
object PutInPutOut extends GameDescription {

  type BoardState = PutInPutOutBoard
  type GameState = PutInPutOutState
  type Move = PutInPutOutMove

  override def newGame: GameState = new PutInPutOutState(new PutInPutOutBoard)
}

/**
 * Represent the only Tiles placeable in [[examples.putinputout.PutInPutOutBoard]].
 */
sealed trait PutInPutOutTile
/**
 * Extends [[examples.putinputout.PutInPutOutTile]].
 * Represents the specific Tile placeable in [[examples.putinputout.PutInPutOutBoard]].
 */
case object TheTile extends PutInPutOutTile

/**
 * Represent the only Pawns playable in [[examples.putinputout.PutInPutOutBoard]].
 */
sealed trait PutInPutOutPawn
/**
 * Extends [[examples.putinputout.PutInPutOutPawn]].
 * Represents the specific Pawn playable in [[examples.putinputout.PutInPutOutBoard]].
 */
case object ThePawn extends PutInPutOutPawn

/**
 * Extends [[sbags.entity.BasicBoard]] defining Tile as [[examples.putinputout.PutInPutOutTile]]
 * and Pawn as [[examples.putinputout.PutInPutOutPawn]].
 */
class PutInPutOutBoard extends BasicBoard {
  type Tile = PutInPutOutTile
  type Pawn = PutInPutOutPawn

  override def tiles: Seq[PutInPutOutTile] = List(TheTile)
}

/**
 * Extends [[sbags.entity.BasicGameState]] defining the type of the Board as
 * [[examples.putinputout.PutInPutOutBoard]].
 *
 * @param putInPutOutBoard represents the [[examples.putinputout.PutInPutOutBoard]] of the game.
 */
class PutInPutOutState(putInPutOutBoard: PutInPutOutBoard) extends BasicGameState(putInPutOutBoard) {
  override type Move = PutInPutOutMove

  override def ruleSet: RuleSet[PutInPutOutMove, PutInPutOutState] = new PutInPutOutRuleSet()
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
 * Extends [[examples.putinputout.PutInPutOutMove]]
 * and represents the Move that inserts [[examples.putinputout.ThePawn]] in [[examples.putinputout.TheTile]]
 */
case object PutIn extends PutInPutOutMove

/**
 * Extends [[examples.putinputout.PutInPutOutMove]]
 * and represents the Move that removes what is placed in [[examples.putinputout.TheTile]]
 * (in [[examples.putinputout.PutInPutOut]] game, the only Pawn available is [[examples.putinputout.ThePawn]])
 */
case object PutOut extends PutInPutOutMove
