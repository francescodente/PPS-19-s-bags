package sbags.core.dsl

trait Accumulator[T, G] {
  def accumulate(fs: Seq[G => T]): G => T
  def neutral(s: G): T
}

trait Modifiers[G] {
  object iterating {
    def over[F](feature: Feature[G, Seq[F]]): Iteration[F] = Iteration(feature)

    case class Iteration[F](feature: Feature[G, Seq[F]]) {
      def as[T](action: F => G => T)(implicit ev: Accumulator[T, G]): G => T =
        g => ev.accumulate(feature(g).map(action))(g)

      def mappedTo[X](f: F => X): Iteration[X] = Iteration(feature map (_ map f))

      def where(f: F => Boolean): Iteration[F] = Iteration(feature map (_ filter f))
    }
  }

  object when {
    def apply[T](predicate: G => Boolean)(action: => G => T)(implicit ev: Accumulator[T, G]): G => T =
      g => if (predicate(g)) action(g) else ev.neutral(g)
  }
}
