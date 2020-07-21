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
    map.filterKeys(_.pattern.asPredicate.test(command))
    .headOption.map(_._2(command))
}

object DefaultEventParser {
  private def tileSelection(x: String): Event = TileSelected(x.split(',')(0).toInt, x.split(',')(1).toInt)
  private def pawnSelection(x: String): Event = PawnSelected(x)
  private def quitSelection(x: String): Event = Done

  private val map = Map("([0-9]+,[0-9]+)".r -> tileSelection _,
    "quit".r -> quitSelection _,
    "([a-z]+)".r -> pawnSelection _)

  def apply(): EventParser = new EventParser(map)
}
