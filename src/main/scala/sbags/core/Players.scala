package sbags.core

trait Players[P, G] {
  def players(state: G): Seq[P]
}
