package sbags.interaction.controller

import org.scalatest.{FlatSpec, Matchers}

import scala.util.matching.Regex

class EventParserTest extends FlatSpec with Matchers {

  behavior of "An event parser"

  val invalidCommand = "invalidCommand"

  val numberString = "123"
  val numbersRegex: Regex = "[0-9]+".r
  case class NumberEvent(number: String) extends Event
  def numberEvent: String => Event = NumberEvent(_)

  it should "not provide any event if the map is empty" in {
    val parser = new EventParser(Map.empty)
    parser.parse(invalidCommand) should be (None)
  }

  it should "provide the correct event when a string matches" in {
    val map: Map[Regex, String => Event] = Map(numbersRegex -> numberEvent)
    val parser = new EventParser(map)
    parser.parse(numberString) should be (Some(NumberEvent(numberString)))
  }

  it should "not provide any event if the input is not recognized" in {
    val map: Map[Regex, String => Event] = Map(numbersRegex -> numberEvent)
    val parser = new EventParser(map)
    parser.parse(invalidCommand) should be (None)
  }

}
