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

TODO: insert game-independent code examples.
