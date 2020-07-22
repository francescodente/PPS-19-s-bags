package sbags.interaction.view.cli

import sbags.interaction.controller.{Done, Event, PawnSelected, TileSelected}

import scala.util.matching.Regex

/**
 * Translates strings in [[sbags.interaction.controller.Event]]s.
 * @param map having regular expressions recognizing the commands as keys,
 * and functions that translate strings in Events as values.
 * Note that events at the beginning of the map have priority over the remaining ones.
 */
class CliEventParser(map: Map[Regex, String => Event]) {
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

object CliEventParser {
  private def tileSelection: String =>  Event = x => TileSelected(x.split(',')(0).toInt - 1, x.split(',')(1).toInt - 1)
  private def pawnSelection: String =>  Event = x => PawnSelected(x)
  private def doneSelection: String =>  Event = _ => Done

  private val defaultMap = Map("([0-9]+,[0-9]+)".r -> tileSelection,
    "done".r -> doneSelection,
    "([a-z]+)".r -> pawnSelection)

  def apply(map: Map[Regex, String => Event] = defaultMap): CliEventParser = new CliEventParser(map)
}
