package sbags.core.extension

trait Players[P, G] {
  def players(state: G): Seq[P]
}
