package sbags.core.dsl

import sbags.core.{BoardGameState, BoardStructure}
import sbags.core.BoardGameState._
import sbags.core.ruleset.RuleSet

import scala.reflect.ClassTag

trait RuleSetBuilder[M, G] extends MovesExecution[M, G] with MovesGeneration[M, G] {
  def ruleSet(fun: => Unit): RuleSet[M, G] = {
    fun
    new RuleSet[M, G] {
      override def availableMoves(state: G): Seq[M] =
        for (gen <- movesGen; move <- gen(state)) yield move

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
      addMoveExe {
        case _: Move => move
      }
  }

}

trait MovesGeneration[M, G] {
  private var stack: List[Generator] = List()
  protected var movesGen: List[G => Seq[M]] = List()

  def addMoveGen(gen: G => Seq[M]): Unit = movesGen = movesGen :+ gen

  case class Generator(f: List[Generator] => G => Seq[M]) {
    protected var subGenerators: List[Generator] = List()

    def moves(state: G): Seq[M] = f(subGenerators)(state)

    def registerSubGenerator(generator: Generator): Unit = subGenerators = subGenerators :+ generator
  }

  def openScope(f: List[Generator] => G => Seq[M]): Unit = {
    val generator = Generator(f)
    for (h <- stack.headOption) h.registerSubGenerator(generator)
    stack = generator :: stack
  }

  def closeScope(): Unit = {
    if (stack.tail.isEmpty) {
      addMoveGen(stack.head.moves)
    }
    stack = stack.tail
  }

  def always(action: => Unit): Unit = scoped(action) { gs =>
    s =>
      gs flatMap (_.moves(s))
  }

  def generate(move: M): Unit = scoped() { _ => _ => Seq(move) }

  def when(predicate: G => Boolean)(action: => Unit): Unit = scoped(action) { gs =>
    s =>
      if (predicate(s)) gs flatMap (_.moves(s))
      else Seq.empty
  }

  private def scoped(action: => Unit)(f: List[Generator] => G => Seq[M]): Unit = {
    openScope(f)
    action
    closeScope()
  }
}
