package sbags.interaction.view.cli

import sbags.interaction.view.Event

import scala.util.matching.Regex

class InputParserBuilder(private val rules: Seq[String => Option[Event]] = Seq.empty) {
  def addRule(rule: String => Option[Event]): InputParserBuilder =
    new InputParserBuilder(rules :+ rule)

  def addFromRegex(regex: Regex, rule: Seq[String] => Event): InputParserBuilder =
    addRule {
      case regex(groups @ _*) => Some(rule(groups))
      case _ => None
    }

  def addKeyword(keyword: String, event: Event): InputParserBuilder =
    addRule {
      case `keyword` => Some(event)
      case _ => None
    }

  def parser: InputParser = InputParser(rules)
}
