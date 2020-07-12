package sbags.entity

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import sbags.entity.Mocks.TestState

class GameEndConditionTest extends FlatSpec with Matchers with MockFactory {
  behavior of "An ended game"

  it should "not execute any other move" in {
    val endedGame = new TestState with GameEndCondition[Int] {
      override def gameResult: Option[Int] = Some(0)
    }
    endedGame.executeMove(None)
  }

  behavior of "An ongoing game"

  it should "execute any valid move" in {
    val ongoingGame = new TestState with GameEndCondition[Int] {
      override def gameResult: Option[Int] = None
    }
    ongoingGame.executeMove(None)
  }
}
