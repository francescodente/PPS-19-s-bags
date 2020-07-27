package sbags.interaction.view.cli

import sbags.interaction.controller.{Done, Event, LaneSelected, PawnSelected, TileSelected}

import scala.util.matching.Regex

/**
 * Translates strings in [[sbags.interaction.controller.Event]]s.
 * @param map having regular expressions recognizing the commands as keys,
 * and functions that translate strings in Events as values.
 * Note that events at the beginning of the map have priority over the remaining ones.
 */
class CliEventParser(map: Map[Regex, String => Event]) {
  private var actualMap: Map[Regex, String => Event] = map

  /**
   * Adds new parsing rules to the one existing
   * @param rules variable argument of parsing rules which have to be added to parser
   */
  def addNewRules(rules : (Regex, String => Event)*): Unit = rules.foreach(kv => actualMap = actualMap + kv)

  /**
   * Parses the given string, providing the matching [[sbags.interaction.controller.Event]]
   * if any, None otherwise.
   *
   * @param command the string to be parsed.
   * @return the matching [[sbags.interaction.controller.Event]] if any, None otherwise.
   */
  def parse(command: String): Option[Event] =
    actualMap.filterKeys(_.pattern.asPredicate.test(command))
    .headOption.map(_._2(command))
}

object CliEventParser {
  private def oneBasedIndex(index: Int): Int = index - 1

  private val tileSelection: String =>  Event = x => TileSelected(oneBasedIndex(x.split(',')(0).toInt), oneBasedIndex(x.split(',')(1).toInt))
  private val pawnSelection: String =>  Event = x => PawnSelected(x)
  private val laneSelection: String =>  Event = x => LaneSelected(oneBasedIndex(x.toInt))
  private val doneSelection: String =>  Event = _ => Done

  private val defaultMap = Map(
    "(^[0-9]+,[0-9]+$)".r -> tileSelection,
    "(^[0-9]+$)".r -> laneSelection,
    "^done$".r -> doneSelection,
    "(^[a-z]+$)".r -> pawnSelection
  )

  def apply(map: Map[Regex, String => Event] = defaultMap): CliEventParser = new CliEventParser(map)

  def empty: CliEventParser = new CliEventParser(Map.empty)
}
