package pacman3d.logic.behaviors

import pacman3d.logic.Direction
import pacman3d.entities.World
import pacman3d.entities.Ghost

object InGhostHouseMovementStrategy: GhostMovementStrategy() {

    // TODO: Make this more robust
    override fun onPositionUpdated(world: World, ghost: Ghost, mazePositionChanged: Boolean) = with (ghost) {
        when {
            position.y <= 16.5 -> nextDirection = Direction.DOWN
            position.y >= 18.0 -> nextDirection = Direction.UP
        }
    }

    override fun onStart(world: World, ghost: Ghost) {
        ghost.nextDirection = Direction.UP
    }
}