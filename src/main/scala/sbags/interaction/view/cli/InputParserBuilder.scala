package sbags.interaction.view.cli

import sbags.interaction.view.Event

/**
 * Aggregates various parsing rules to build a parser and provides it as requested.
 *
 * @param partialParser initial parsing rules.
 */
class InputParserBuilder(private val partialParser: PartialFunction[String, Event] = PartialFunction.empty) {
  /**
   * Returns a new InputParserBuilder with a new parsing rule that will be after the ones already defined.
   * The new parsing rule have to match a 'keyword'.
   *
   * @param keyword the word needed to trigger the event.
   * @param event the event triggered each time is parsed the keyword.
   * @return a new InputParserBuilder with the new rule.
   */
  def addKeyword(keyword: String, event: Event): InputParserBuilder =
    addRule {
      case `keyword` => event
    }

  /**
   * Returns a new InputParserBuilder with a new parsing rule that will be after the ones already defined.
   *
   * @param rule the new parsing rule
   * @return a new InputParserBuilder with the new rule.
   */
  def addRule(rule: PartialFunction[String, Event]): InputParserBuilder =
    new InputParserBuilder(partialParser orElse rule)

  /** Returns the function representing the parsing rules aggregated in the InputParserBuilder. */
  def parser: String => Option[Event] = partialParser.lift
}
