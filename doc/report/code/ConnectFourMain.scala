object ConnectFourMain extends App {
  AppRunner run ConnectFourSetup
}

object ConnectFourSetup extends CliGameSetup[Move, State] with RectangularBoardSetup[BoardStructure, State] {
    ...
}