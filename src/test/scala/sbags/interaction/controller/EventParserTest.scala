package sbags.interaction.controller

import org.scalatest.{FlatSpec, Matchers}

import scala.util.matching.Regex

class EventParserTest extends FlatSpec with Matchers {
  val invalidCommand = "invalidCommand"

  private val numberString = "123"
  private val numbersRegex = "[0-9]+".r
  case class NumberEvent(number: String) extends Event
  private def numberEvent = NumberEvent(_)

  private val lowercaseString = "abc"
  private val lowercaseRegex = "[a-z]+".r
  case class LowercaseEvent(s: String) extends Event
  private def lowercaseEvent = LowercaseEvent(_)

  private val prioritizedCommand = "importantCommand"
  private val prioritizedCommandRegex = "important[A-Z|a-z]+".r
  case class ImportantEvent(c: String) extends Event
  private def importantEvent = ImportantEvent(_)

  behavior of "An event parser"

  it should "not provide any event if the map is empty" in {
    val parser = new EventParser(Map.empty)
    parser.parse(invalidCommand) should be (None)
  }

  it should "provide the correct event when a string matches" in {
    val map = Map(numbersRegex -> numberEvent)
    val parser = new EventParser(map)
    parser.parse(numberString) should be (Some(NumberEvent(numberString)))
  }

  it should "not provide any event if the input is not recognized" in {
    val map = Map(numbersRegex -> numberEvent)
    val parser = new EventParser(map)
    parser.parse(invalidCommand) should be (None)
  }

  it should "provide the correct event if it has multiple elements in the initial map" in {
    val map = Map(numbersRegex -> numberEvent, lowercaseRegex -> lowercaseEvent)
    val parser = new EventParser(map)
    parser.parse(lowercaseString) should be (Some(LowercaseEvent(lowercaseString)))
  }

  it should "understand commands priority" in {
    val map = Map(prioritizedCommandRegex -> importantEvent,
      numbersRegex -> numberEvent,
      lowercaseRegex -> lowercaseEvent)
    val parser = new EventParser(map)
    parser.parse(prioritizedCommand) should be (Some(ImportantEvent(prioritizedCommand)))
  }

}