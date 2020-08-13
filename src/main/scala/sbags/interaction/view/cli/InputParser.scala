package sbags.interaction.view.cli

import sbags.interaction.view.{Event, LaneSelected, PawnSelected, Quit, TileSelected}

import scala.util.matching.Regex

trait InputParser {
  /**
   * Parses the given string, providing the matching [[Event]]
   * if any, None otherwise.
   *
   * @param command the string to be parsed.
   * @return the matching [[Event]] if any, None otherwise.
   */
  def parse(command: String): Option[Event]
}

object InputParser {
  class RegexInputParser(rules: Seq[String => Option[Event]]) extends InputParser {
    def parse(command: String): Option[Event] =
      rules.map(_.apply(command)).find(_.isDefined).flatten
  }

  private def oneBasedIndex(index: Int): Int = index - 1

  private val tileSelection: String => Event = x => TileSelected(oneBasedIndex(x.split(',')(0).toInt), oneBasedIndex(x.split(',')(1).toInt))
  private val pawnSelection: String => Event = x => PawnSelected(x)
  private val laneSelection: String => Event = x => LaneSelected(oneBasedIndex(x.toInt))
  private val quitSelection: String => Event = _ => Quit

  private val defaultMap = Seq(
    "(^quit$)".r -> quitSelection,
    "^([0-9]+),([0-9]+)$".r -> tileSelection,
    "(^[0-9]+$)".r -> laneSelection,
    "(^[a-z]+$)".r -> pawnSelection
  )

  def apply(rules: Seq[String => Option[Event]]): InputParser = new RegexInputParser(rules)

  def empty: InputParser = new RegexInputParser(Seq.empty)
}

