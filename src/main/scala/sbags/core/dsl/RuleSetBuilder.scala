package sbags.core.dsl

import sbags.core.ruleset.RuleSet

import scala.reflect.ClassTag

trait RuleSetBuilder[M, G] extends MovesExecution[M, G] with MovesGeneration[M, G] with Features[G] {
  def ruleSet(fun: => Unit): RuleSet[M, G] = {
    fun
    new RuleSet[M, G] {
      override def availableMoves(state: G): Seq[M] =
        generateMoves(new GenerationContext(state))

      override def executeMove(move: M)(state: G): G =
        movesExe.reduce(_ orElse _)(move)(state)
    }
  }
}

trait MovesExecution[M, G] {
  protected var movesExe: List[PartialFunction[M, G => G]] = List()

  def addMoveExe(moveExe: PartialFunction[M, G => G]): Unit = movesExe = movesExe :+ moveExe

  object onMove {
    def apply(m: M): (G => G) => Unit =
      g => addMoveExe { case `m` => g }

    def matching(f: PartialFunction[M, G => G]): Unit =
      addMoveExe(f)

    def ofType[Move <: M : ClassTag](move: G => G): Unit =
      addMoveExe { case _: Move => move }
  }
}

object MovesGeneration {
  implicit def featureToFeatureValue[G, F](feature: Feature[G, F])(implicit ev: StateContext[G]): F =
    feature(ev.state)
}

class StateContext[G](val state: G)

class GenerationContext[M, G](state: G) extends StateContext(state) {
  private var movesStream: Stream[M] = Stream.empty
  def addMoves(g: Seq[M]): Unit = movesStream = g.toStream ++ movesStream
  def moves: Stream[M] = movesStream
}

trait MovesGeneration[M, G] {
  private var generators: List[GenerationContext[M, G] => Unit] = List()

  def moveGeneration(g: GenerationContext[M, G] => Unit): Unit = generators = generators :+ g

  def generateMoves(ctx: GenerationContext[M, G]): Stream[M] = {
    generators.foreach(_(ctx))
    ctx.moves
  }

  object iterating {
    def over[F](feature: Feature[G, Seq[F]]): Iteration[F] = Iteration(feature)

    case class Iteration[F](feature: Feature[G, Seq[F]]) {
      def as(action: F => Unit)(implicit ctx: GenerationContext[M, G]): Unit =
        feature(ctx.state).foreach(action)

      def mappedTo[X](f: F => X): Iteration[X] = Iteration(feature map (_ map f))

      def where(f: F => Boolean): Iteration[F] = Iteration(feature map (_ filter f))
    }
  }

  object generate {
    def move(m: M)(implicit ctx: GenerationContext[M, G]): Unit = moves(m)
    def moves(m: M*)(implicit ctx: GenerationContext[M, G]): Unit = ctx.addMoves(m)
  }

  object when {
    def apply(predicate: G => Boolean)(action: => Unit)(implicit ctx: GenerationContext[M, G]): Unit =
      if (predicate(ctx.state)) action
  }
}
