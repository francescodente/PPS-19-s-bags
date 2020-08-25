package sbags.model.dsl

/** Groups utilities to deal with Chainable objects. */
object Chainables {
  /**
   * A type class that gives a type T the ability to be chained to other elements of the same type,
   * as well as declaring T a transformation from a type A to a type B.
   *
   * @tparam T the transformation type.
   * @tparam A the domain of the transformation.
   * @tparam B the codomain of the transformation.
   */
  trait Chainable[T, A, B] {
    /**
     * Combines to Ts to produce a new T, which is a concatenation of the two operands.
     *
     * @param t1 the first operand.
     * @param t2 the second operand.
     * @return a new T produced by combining t1 and t2.
     */
    def chain(t1: T, t2: T): T

    /**
     * Creates a chainable transformation that has the same effects as f when applied to an element
     * of type A.
     *
     * @param f the function on which the transformation should be based.
     * @return a new T derived from f.
     */
    def unit(f: A => B): T

    /**
     * Defines the neutral element for the chain operation, that is the element for which chain has
     * no effect when applied to the left or to the right of another element.
     *
     * @return a T that is the neutral element for the chain operation.
     */
    def neutral: T

    /**
     * Applies the transformation wrapped inside t to the given element of type A.
     *
     * @param t the transformation to be applied.
     * @param a the element on which to apply the transformation.
     * @return the element produced by the transformation.
     */
    def transform(t: T)(a: A): B
  }

  /**
   * Defines methods that are implicitly added to elements for which an instance of
   * [[sbags.model.dsl.Chainables.Chainable]] is defined.
   */
  implicit class ChainableOps[T, A, B](self: T)(implicit ev: Chainable[T, A, B]) {
    /**
     * Performs the chain operation as a binary infix operator, enabling the following syntax:
     *
     * {{{
     *   val result = first and second
     *                      ^
     * }}}
     *
     * @param other the second operand.
     * @return the result of the chain operation.
     */
    def and(other: T): T = ev.chain(self, other)

    /**
     * Applies the transformation wrapped in this instance, using the function call syntax.
     *
     * @param a the element on which to apply the transformation.
     * @return the result of the transform operation.
     */
    def apply(a: A): B = ev.transform(self)(a)
  }
}
