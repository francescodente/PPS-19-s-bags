package sbags.core.dsl

case class Action[G](run: G => G) {
  def >>(other: Action[G]): Action[G] = Action(g => other.run(run(g)))
}

trait Actions[G] {
}
