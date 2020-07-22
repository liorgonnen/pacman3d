package pacman3d.logic.behaviors

import pacman3d.logic.Direction
import pacman3d.logic.Direction.LEFT
import pacman3d.state.GameState
import pacman3d.state.GhostState

abstract class GhostBehaviorMode {
    open fun onPositionUpdated(game: GameState, ghost: GhostState, mazePositionChanged: Boolean) = Unit
    open fun onStart(game: GameState, ghost: GhostState) = Unit
}

//class ChaseMode : GhostBehaviorMode() {
//
//}

//class FrightenedMode: GhostBehaviorMode() {
//
//}