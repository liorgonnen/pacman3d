package pacman3d.logic.behaviors

import pacman3d.state.World
import pacman3d.state.GhostState

abstract class GhostBehaviorMode {
    open fun onPositionUpdated(world: World, ghost: GhostState, mazePositionChanged: Boolean) = Unit
    open fun onStart(world: World, ghost: GhostState) = Unit
}

//class ChaseMode : GhostBehaviorMode() {
//
//}

//class FrightenedMode: GhostBehaviorMode() {
//
//}