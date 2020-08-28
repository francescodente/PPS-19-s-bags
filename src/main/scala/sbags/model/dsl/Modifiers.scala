package sbags.model.dsl

import sbags.model.dsl.Chainables._

/**
 * Trait that provides a domain specific language (DSL) for state iteration and state conditions.
 *
 * @tparam G type of the game state.
 */
trait Modifiers[G] {

  /**
   * Enables syntax to iterate over collections extracted from a state.
   *
   * <p>
   * This method supports syntax such as the following:
   * </p>
   * {{{
   *   iterating over SomeFeature as { elem => ... }
   *   ^
   * }}}
   */
  object iterating {
    def over[F](feature: Feature[G, Traversable[F]]): Iteration[F] = Iteration(feature)

    /** Represents the Iteration available method. Don't create this by yourself. Use the "iterating over" syntax instead. */
    case class Iteration[F](feature: Feature[G, Traversable[F]]) {
      /**
       * Specifies the iteration function, which is a function that produces a chainable element from each of
       * the items returned by the feature. The result of this method is given by the result of chaining the results of
       * each individual value returned by the iteration function.
       *
       * @param it the iteration function.
       * @param ev a [[sbags.model.dsl.Chainables.Chainable]] instance
       * @tparam T the type for which the chainable is defined.
       * @tparam B the return type of the chainable transformation.
       * @return an element T obtained with the concatenation of all iterations.
       */
      def as[T, B](it: F => T)(implicit ev: Chainable[T, G, B]): T =
        ev.unit(g => feature(g).map(it).fold(ev.neutral)(_ and _)(g))
    }
  }

  /**
   * Enables syntax to express conditions on properties of the state.
   *
   * <p>
   * This method supports syntax such as the following:
   * </p>
   * {{{
   *   when (predicateFeature) {...}
   *   ^
   * }}}
   */
  object when {
    /**
     * Applies the when keyword to a predicate, expressed as feature extracting a Boolean value from the state.
     * This operation returns a new chainable transformation of type T, which uses the given predicate to determine
     * the result of the transformation. If the predicate evaluates to true, the given action is used to perform
     * the transformation; otherwise, the neutral element established by the [[sbags.model.dsl.Chainables.Chainable]]
     * instance is used.
     *
     * @param p the feature representing the predicate.
     * @param action the action to be performed if the condition is true.
     * @param ev the [[sbags.model.dsl.Chainables.Chainable]] instance.
     * @tparam T the type of [[sbags.model.dsl.Chainables.Chainable]] to be used.
     * @tparam B return type of the transformation.
     * @return a new transformation of type T that evaluates the given predicate to determine the result.
     */
    def apply[T, B](p: Feature[G, Boolean])(action: => T)(implicit ev: Chainable[T, G, B]): T = {
      ev.unit { g =>
        if (p(g)) ev.transform(action)(g)
        else ev.transform(ev.neutral)(g)
      }
    }
  }
}
