package sbags.interaction.controller

import scala.util.matching.Regex

class EventParser(map: Map[Regex, String => Event]) {

  def parse(command: String): Option[Event] =
    map.filterKeys(_.pattern.asMatchPredicate().test(command))
    .headOption.map(_._2(command))

}
