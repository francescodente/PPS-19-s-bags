package sbags.interaction.view.cli

trait Converter[A] {
  def toString(a: A): String
  def fromString(s: String): A
}

object Converters {
  val letters: Converter[Int] = new Converter[Int] {
    override def toString(a: Int): String = ('a' + a).toChar.toString

    override def fromString(s: String): Int = s.toLowerCase.charAt(0) - 'a'
  }

  def withOffset(offset: Int): Converter[Int] = new Converter[Int] {
    override def toString(a: Int): String = (a + offset).toString

    override def fromString(s: String): Int = s.toInt - offset
  }

  val oneBased: Converter[Int] = withOffset(1)
}
