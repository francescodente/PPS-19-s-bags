# PPS-19-s-bags

**Scala Board-based Abstract Games of Strategy** (sbags for short) is a library for developing board based abstract strategy games like chess, checkers, tic-tac-toe and more.

## Features

THe library is written in **scala** and provides the user with an environment in which functional programming is blended with object oriented design to achieve flexibility and extensibility.

Sbags offers the features and abstractions common to the majority of the abstract board games:

* A **board** where the **player** can place **pawns** on **tiles** via **moves**;

* A **ruleset** that enables easy writing of game rules via an internal **DSL**;

* Extensions to handle **players**, **turns**, **ending conditions** and **game results**;

* An interaction layer with a CLI view already implemented that enables the user to display the game he just created with two requirements only: the board must be **rectangular** and he must provide a mapping between its pawns and their **ascii** representation.

## Requirements

To use the library you need the following software installed:

* Scala version 2.12.11
* Sbt version 1.3.13

An IDE is strongly recommended.

## How to use

### Clone

```bash
git clone https://github.com/francescodente/PPS-19-s-bags.git
```

### Import

Open the project in your favorite IDE

### Code

You're ready to code!

TODO: explain usage with jar.

## Examples

In this section an overview of all the steps to perform to write a game with the library are provided without referencing any particular game.
Here, two players are alternating placing different pawns on a squared board.
The user interface is a command line.

When writing a game using sbags you start by first defining the entities:

1. Pawn
2. Move
3. Board
4. State

```scala
sealed trait Pawn
case object X extends Pawn
case object O extends Pawn

object Pawn {
  def opponent(pawn: Pawn): Pawn = pawn match {
    case X => O
    case O => X
  }
}

sealed trait Move
case class Put(tile: Coordinate) extends Move

object SquaredBoard extends SquareStructure(SampleGameDescription.size) {
  type Pawn = Pawn
}

case class GameState(board: Board[SampleGameDescription.BoardStructure], currentPlayer: Pawn)
```

Then, you write its **ruleset**, specifying:

1. What happens when a move is executed
2. What happens after each move
3. Under which circumstances a move is available

```scala
object GameRuleSet extends RuleSet[Move, State] with RuleSetBuilder[Move, State] {
  onMove matching {
    case Put(t) =>
      > place currentTurn on t
  }

  after each move -> changeTurn

  moveGeneration {
    iterating over emptyTiles as { t =>
      generate (Put(t))
    }
  }
}
```

After that, the **game description** is defined, which contains any additional variables and implicits that will enable the extensions.

```scala
object SampleGameDescription extends GameDescription[Move, GameState] {
  val size = 3
  private val players: Seq[Pawn] = Seq(X, O)

  type BoardStructure = SquaredBoard.type

  override def initialState: State = GameState(Board(SquaredBoard), X)

  override val ruleSet: RuleSet[Move, State] = GameRuleSet

  implicit lazy val boardState: BoardState[BoardStructure, State] =
    BoardState((s, b) => s.copy(board = b))

  implicit lazy val turns: PlayersAsTurns[Pawn, State] =
    PlayersAsTurns.roundRobin(_ => players, (s,p) => s.copy(currentPlayer = p))

}
```

You're almost done, but a game setup will greatly simplify the main.
Here you can specify which renderers are available, what commands the parser should recognize (in case you are using a cli view, as in the example), and how event sequences should match to moves.

```scala
object GameSetup extends CliGameSetup[Move, GameState] with RectangularBoardSetup[BoardStructure, GameState] {
  override val gameDescription: GameDescription[Move, GameState] = SampleGameDescription

  override def setupRenderers(rendering: RendererBuilder[State, CliRenderer[State]]): RendererBuilder[State, CliRenderer[State]] = rendering
    .withBoard
    .withTurns

  override def setupInputParser(builder: InputParserBuilder): InputParserBuilder = builder
    .addTileCommand()

  override def eventsToMove(events: List[Event]): Option[Move] = events match {
    case TileSelected(x, y) :: Nil => Some(Put(x, y))
    case _ => None
  }
}
```

Now that everything is set up the main is just a piece of cake:

```scala
object Main extends App {
  AppRunner run GameSetup
}
```
