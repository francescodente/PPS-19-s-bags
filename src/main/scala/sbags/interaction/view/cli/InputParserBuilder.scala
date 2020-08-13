package sbags.interaction.view.cli

import sbags.interaction.view.Event

class InputParserBuilder(private val partialParser: PartialFunction[String, Event] = PartialFunction.empty) {
  def addRule(rule: PartialFunction[String, Event]): InputParserBuilder =
    new InputParserBuilder(partialParser orElse rule)

  def addKeyword(keyword: String, event: Event): InputParserBuilder =
    addRule {
      case `keyword` => event
    }

  def parser: String => Option[Event] = partialParser.lift
}
