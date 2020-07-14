package sbags.interaction.controller

import scala.util.matching.Regex

class EventParser(map: Map[Regex, Event]) {

  def parse(command: String): Option[Event] = ???

}
