package pacman3d.logic.behaviors

import pacman3d.logic.Direction
import pacman3d.state.World
import pacman3d.state.GhostState

object InGhostHouse: GhostBehaviorMode() {

    override fun onPositionUpdated(world: World, ghost: GhostState, mazePositionChanged: Boolean) = with (ghost) {
        when {
            position.y <= 16.5 -> nextDirection = Direction.DOWN
            position.y >= 18.0 -> nextDirection = Direction.UP
        }
    }

    override fun onStart(world: World, ghost: GhostState) {
        ghost.nextDirection = Direction.UP
    }
}