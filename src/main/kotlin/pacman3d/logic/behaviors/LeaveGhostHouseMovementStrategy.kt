package pacman3d.logic.behaviors

import pacman3d.logic.Direction
import pacman3d.entities.World
import pacman3d.entities.Ghost

object LeaveGhostHouseMovementStrategy: GhostMovementStrategy() {

    override fun onPositionUpdated(world: World, ghost: Ghost, mazePositionChanged: Boolean) = with (ghost) {
        when {
            position.mazeX < 14 -> nextDirection = Direction.RIGHT
            position.mazeX > 15 -> nextDirection = Direction.LEFT
            position.x in 14.5..15.5 -> nextDirection = Direction.UP
        }
    }

    override fun hasReachedTarget(ghost: Ghost) = ghost.position.mazeY == 14
}