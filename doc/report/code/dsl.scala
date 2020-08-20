case class State(fruits: Seq[String])

object iterating {
  def over[F](feature: Feature[State, Traversable[F]]): Iteration[F] =
    Iteration(feature)

  case class Iteration[F](feature: Feature[State, Traversable[F]]) {
    def as[T, B](it: F => T)(implicit ev: Chainable[T, State, B]): T =
      ev.unit(s => feature(s).map(it).fold(ev.neutral)(ev.chain)(s))
  }
}

...

val fruits: Feature[State, Seq[String]] = Feature(_.fruits)

iterating over fruits as { fruit =>
  generate (Eat(fruit))
}