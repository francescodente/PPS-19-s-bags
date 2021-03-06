package sbags.interaction.view.cli

import org.scalatest.{FlatSpec, Matchers}
import sbags.interaction.view.Event

class InputParserBuilderTest extends FlatSpec with Matchers {

  case object EventA extends Event

  case object EventB extends Event

  private val commandA = "a"
  private val commandB = "b"

  behavior of "Generic rules"

  they should "be applied when parsing a command" in {
    new InputParserBuilder().addRule {
      case `commandA` => EventA
    }.parser(commandA) should be(Some(EventA))
  }

  they should "not parse an unspecified command" in {
    new InputParserBuilder().addRule {
      case `commandA` => EventA
    }.parser(commandB) should be(None)
  }

  they should "be applied in order" in {
    new InputParserBuilder().addRule {
      case `commandA` => EventA
    }.addRule {
      case `commandA` => EventB
    }.parser(commandA) should be(Some(EventA))
  }

  behavior of "Keyword rules"

  they should "match if the command equals the keyword" in {
    new InputParserBuilder().addKeyword(commandA, EventA).parser(commandA) should be(Some(EventA))
  }

  they should "not match if the command is not equal to the keyword" in {
    new InputParserBuilder().addKeyword(commandA, EventA).parser(commandB) should be(None)
  }
}
