package sbags.interaction.controller

import scala.util.matching.Regex

/**
 * Translates strings in [[sbags.interaction.controller.Event]]s.
 * @param map having regular expressions recognizing the commands as keys,
 * and functions that translate strings in Events as values.
 * Note that events at the beginning of the map have priority over the remaining ones.
 */
class EventParser(map: Map[Regex, String => Event]) {

  /**
   * Parses the given string, providing the matching [[sbags.interaction.controller.Event]]
   * if any, None otherwise.
   *
   * @param command the string to be parsed.
   * @return the matching [[sbags.interaction.controller.Event]] if any, None otherwise.
   */
  def parse(command: String): Option[Event] =
    map.filterKeys(_.pattern.asMatchPredicate().test(command))
    .headOption.map(_._2(command))

}
