package sbags.interaction.view.cli

/**
 * Defines the Conversion from each entity to String.
 *
 * @tparam E type of the entity that need to be converted.
 */
trait Converter[E] {
  /**
   * Defines the String associated to an entity.
   *
   * @param a the entity which needs to be printed.
   * @return the String representing a.
   */
  def toString(a: E): String

  /** Returns the PartialFunction that can convert the entity associated to a string. */
  def fromString: PartialFunction[String, E]
}

/** Provides some Converter examples */
object Converter {
  private val letter = """^\s*([a-zA-Z])\s*$""".r
  private val digits = """^\s*([0-9]+)\s*$""".r

  /** Defines the conversion from the number of a letter to the letter. */
  val letters: Converter[Int] = new Converter[Int] {
    override def toString(a: Int): String = ('a' + a).toChar.toString

    override def fromString: PartialFunction[String, Int] = {
      case letter(l) => l.toLowerCase.charAt(0) - 'a'
    }
  }

  /** Defines the conversion from a number an other number with a determinate offset. */
  def withOffset(offset: Int): Converter[Int] = new Converter[Int] {
    override def toString(a: Int): String = (a + offset).toString

    override def fromString: PartialFunction[String, Int] = {
      case digits(d) => d.toInt - offset
    }
  }

  /** Defines the conversion from a number an other number with one as offset. */
  val oneBased: Converter[Int] = withOffset(1)
}
