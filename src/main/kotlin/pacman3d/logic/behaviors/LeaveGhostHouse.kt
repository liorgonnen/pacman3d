package pacman3d.logic.behaviors

import pacman3d.logic.Direction
import pacman3d.entities.World
import pacman3d.entities.Ghost

object LeaveGhostHouse: GhostBehaviorMode() {

    override fun onPositionUpdated(world: World, ghost: Ghost, mazePositionChanged: Boolean) = with (ghost) {
        when {
            position.mazeY == 14 -> ghost.setMode(ChaseMode, world)
            position.mazeX < 14 -> nextDirection = Direction.RIGHT
            position.mazeX > 15 -> nextDirection = Direction.LEFT
            position.x in 14.5..15.5 -> nextDirection = Direction.UP
        }
    }
}