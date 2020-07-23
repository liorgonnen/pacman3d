package pacman3d.logic.behaviors

import pacman3d.logic.Direction
import pacman3d.state.World
import pacman3d.state.Ghost

object LeaveGhostHouse: GhostBehaviorMode() {

    override fun onPositionUpdated(world: World, ghost: Ghost, mazePositionChanged: Boolean) = with (ghost) {
        when {
            position.mazeY == 14 -> ghost.setMode(ScatterMode(), world)
            position.mazeX < 13 -> nextDirection = Direction.RIGHT
            position.mazeX > 14 -> nextDirection = Direction.LEFT
            position.x in 13.5..14.5 -> nextDirection = Direction.UP
        }
    }
}