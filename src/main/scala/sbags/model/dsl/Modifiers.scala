package sbags.model.dsl

import sbags.model.dsl.Chainables._

/**
 * Trait that provides a domain specific language (DSL) for state iteration and state conditions.
 *
 * @tparam G type of the game state.
 */
trait Modifiers[G] {

  /**
   * Enables syntax to work with state iteration.
   *
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   iterating over SomeFeature as { elem => ... }
   *   ^
   * }}}
   * <p>
   *   iterating also support 'mappedTo' and 'where' between 'over' and 'as' word.
   * </p>
   * {{{
   *   iterating over SomeFeature where (elem => true) as {...}
   *                              ^
   * }}}
   * <p>
   *   or
   * </p>
   * {{{
   * iterating over SomeFeature mappedTo (elem => NewElem) as {...}
   *                            ^
   * }}}
   * <p>
   *   'mappedTo' and 'where' could also be used together.
   * </p>
   */
  object iterating {
    def over[F](feature: Feature[G, Traversable[F]]): Iteration[F] = Iteration(feature)

    case class Iteration[F](feature: Feature[G, Traversable[F]]) {
      def as[T, B](action: F => T)(implicit ev: Chainable[T, G, B]): T =
        ev.unit(g => {
          feature(g).map(action).fold(ev.neutral)(_ and _)(g)
        })

      def mappedTo[X](f: F => X): Iteration[X] = Iteration(feature map (_ map f))

      def where(f: F => Boolean): Iteration[F] = Iteration(feature map (_ filter f))
    }
  }

  /**
   * Enables syntax to work with state conditions.
   *
   * <p>
   *  This method supports syntax such as the following:
   * </p>
   * {{{
   *   when (state => true) {...}
   *   ^
   * }}}
   */
  object when {
    def apply[T, B](predicate: G => Boolean)(action: => T)(implicit ev: Chainable[T, G, B]): T = {
      ev.unit { g =>
        if (predicate(g)) ev.transform(action)(g)
        else ev.transform(ev.neutral)(g)
      }
    }
  }
}
