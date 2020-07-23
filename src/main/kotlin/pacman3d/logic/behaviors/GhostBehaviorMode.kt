package pacman3d.logic.behaviors

import pacman3d.state.World
import pacman3d.state.Ghost

abstract class GhostBehaviorMode {
    open fun onPositionUpdated(world: World, ghost: Ghost, mazePositionChanged: Boolean) = Unit
    open fun onStart(world: World, ghost: Ghost) = Unit
}

//class ChaseMode : GhostBehaviorMode() {
//
//}

//class FrightenedMode: GhostBehaviorMode() {
//
//}