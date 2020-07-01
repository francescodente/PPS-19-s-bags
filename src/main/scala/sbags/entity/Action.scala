package sbags.entity

trait Action[T, P] {
  def execute(board: Board[T, P]): Board[T, P]
}
