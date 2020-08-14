package sbags.interaction.view.cli

trait Converter[A] {
  def toString(a: A): String
  def fromString: PartialFunction[String, A]
}

object Converters {
  private val letter = """^\s*([a-zA-Z])\s*$""".r
  private val digits = """^\s*([0-9]+)\s*$""".r

  val letters: Converter[Int] = new Converter[Int] {
    override def toString(a: Int): String = ('a' + a).toChar.toString

    override def fromString: PartialFunction[String, Int] = {
      case letter(l) => l.toLowerCase.charAt(0) - 'a'
    }
  }

  def withOffset(offset: Int): Converter[Int] = new Converter[Int] {
    override def toString(a: Int): String = (a + offset).toString

    override def fromString: PartialFunction[String, Int] = {
      case digits(d) => d.toInt - offset
    }
  }

  val oneBased: Converter[Int] = withOffset(1)
}
