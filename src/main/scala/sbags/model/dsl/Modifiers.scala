package sbags.model.dsl

import sbags.model.dsl.Chainables._

trait Modifiers[G] {
  object iterating {
    def over[F](feature: Feature[G, Seq[F]]): Iteration[F] = Iteration(feature)

    case class Iteration[F](feature: Feature[G, Seq[F]]) {
      def as[T, B](action: F => T)(implicit ev: Chainable[T, G, B]): T =
        ev.unit(g => {
          feature(g).map(action).fold(ev.neutral)(_ and _)(g)
        })

      def mappedTo[X](f: F => X): Iteration[X] = Iteration(feature map (_ map f))

      def where(f: F => Boolean): Iteration[F] = Iteration(feature map (_ filter f))
    }
  }

  object when {
    def apply[T, B](predicate: G => Boolean)(action: => T)(implicit ev: Chainable[T, G, B]): T = {
      ev.unit { g =>
        if (predicate(g)) ev.transform(action)(g)
        else ev.transform(ev.neutral)(g)
      }
    }
  }
}
