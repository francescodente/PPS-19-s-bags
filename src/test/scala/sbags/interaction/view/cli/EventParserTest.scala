package sbags.interaction.view.cli

import org.scalatest.{FlatSpec, Matchers}
import sbags.interaction.view.Event

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

  private def newParser(map: Map[Regex, String => Event]) = CliEventParser(map)

  behavior of "An event parser"

  it should "not provide any event if the map is empty" in {
    val parser = CliEventParser.empty
    parser.parse(invalidCommand) should be (None)
  }

  it should "be able to provide the event corresponding an added rule" in {
    val parser = CliEventParser.empty
    parser.addNewRules(lowercaseRegex -> lowercaseEvent)
    parser.parse(lowercaseString) should be (Some(LowercaseEvent(lowercaseString)))
  }

  it should "provide the correct event when a string matches" in {
    val map: Map[Regex, String => NumberEvent] = Map(numbersRegex -> numberEvent)
    val parser = newParser(map)
    parser.parse(numberString) should be (Some(NumberEvent(numberString)))
  }

  it should "not provide any event if the input is not recognized" in {
    val map = Map(numbersRegex -> numberEvent)
    val parser = newParser(map)
    parser.parse(invalidCommand) should be (None)
  }

  it should "provide the correct event if it has multiple elements in the initial map" in {
    val map = Map(numbersRegex -> numberEvent, lowercaseRegex -> lowercaseEvent)
    val parser = newParser(map)
    parser.parse(lowercaseString) should be (Some(LowercaseEvent(lowercaseString)))
  }

  it should "understand commands priority" in {
    val map = Map(prioritizedCommandRegex -> importantEvent,
      numbersRegex -> numberEvent,
      lowercaseRegex -> lowercaseEvent)
    val parser = newParser(map)
    parser.parse(prioritizedCommand) should be (Some(ImportantEvent(prioritizedCommand)))
  }

}
