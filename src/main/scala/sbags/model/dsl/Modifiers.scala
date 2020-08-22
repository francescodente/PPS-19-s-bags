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

    /** This class represents the Iteration available method, don't create this by yourself. Use instead the syntax iterating over of the DSL. */
    case class Iteration[F](feature: Feature[G, Traversable[F]]) {
      /** TODO
       * This ward close the iteration and give access to the feature value.
       *
       * @param action the action to be performed on the extracted value.
       * @param ev an implicit chainable item.
       * @tparam T The type for what the chainable is defined.
       * @tparam B The exit type of the chainable transformation.
       * @return an element T obtained with the concatenation of all the evaluated value.
       */
      def as[T, B](action: F => T)(implicit ev: Chainable[T, G, B]): T =
        ev.unit(g => {
          feature(g).map(action).fold(ev.neutral)(_ and _)(g)
        })

      /**
       * Map feature value of the iteration.
       * This method could be chained anywhere between the ward 'over' and the ward 'as'.
       *
       * @param f the mapping function.
       * @tparam X the new type of the feature.
       * @return a new [[sbags.model.dsl.Modifiers.iterating.Iteration]] represent the mapped value.
       */
      def mappedTo[X](f: F => X): Iteration[X] = Iteration(feature map (_ map f))

      /**
       * Apply a filter at the iteration.
       *
       * @param f the filter function. Should return true if the value is valid.
       * @return a new [[sbags.model.dsl.Modifiers.iterating.Iteration]] represent filtered value.
       */
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
    /**
     * Method to use the ward [[sbags.model.dsl.Modifiers.when]].
     *
     * @param predicate the predicate indicating whether the condition is satisfied.
     * @param action the action to be performed if the condition is true.
     * @param ev implicit chainable than explain how to merge multiple value T.
     * @tparam T result of the action.
     * @tparam B return type of the transformation T from G to B
     * @return an element of T, ev.neutral if no value is present, the concatenation of the available transformation otherwise.
     */
    def apply[T, B](predicate: G => Boolean)(action: => T)(implicit ev: Chainable[T, G, B]): T = {
      ev.unit { g =>
        if (predicate(g)) ev.transform(action)(g)
        else ev.transform(ev.neutral)(g)
      }
    }
  }
}
