package sbags.model.dsl

object Chainables {
  trait Chainable[T, A, B] {
    def chain(t1: T, t2: T): T
    def unit(f: A => B): T
    def neutral: T
    def transform(t: T)(a: A): B
  }
  
  implicit class ChainableOps[T, A, B](self: T)(implicit ev: Chainable[T, A, B]) {
    def and(other: T): T = ev.chain(self, other)
    def apply(a: A): B = ev.transform(self)(a)
  }
}
