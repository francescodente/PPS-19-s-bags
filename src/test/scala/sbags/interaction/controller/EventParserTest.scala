package sbags.interaction.controller

import org.scalatest.{FlatSpec, Matchers}

class EventParserTest extends FlatSpec with Matchers {

  behavior of "An event parser"

  val invalidCommand = "invalidCommand"

  it should "not find any action if the input is not recognized" in {
    val parser = new EventParser(Map.empty)
    parser.parse(invalidCommand) should be (None)
  }

}
