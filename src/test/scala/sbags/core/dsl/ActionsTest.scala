package sbags.core.dsl

import org.scalatest.{FlatSpec, Matchers}

class ActionsTest extends FlatSpec with Matchers {
  behavior of "An action"

  it can "be concatenated to another action" in {
    val a1 = Action[String](_ + "abc")
    val a2 = Action[String](_ + "def")
    (a1 >> a2).run("xyz") should be ("xyzabcdef")
  }
}
