package sbags.control

import sbags.{control, entity}

trait Game {
  type Board <: entity.Board

  type GameState <: control.GameState[Board]
}
