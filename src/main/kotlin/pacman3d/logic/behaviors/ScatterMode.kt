package pacman3d.logic.behaviors

import pacman3d.logic.Position
import pacman3d.logic.Direction
import pacman3d.logic.Direction.*
import pacman3d.entities.World
import pacman3d.entities.Ghost

object ChaseMode : GhostBehaviorWithLookAhead() {

    override fun updateTargetTile(world: World, ghost: Ghost, targetTile: Position) {
        ghost.getChaseTargetTile(world, targetTile)
    }
}

object ScatterMode : GhostBehaviorWithLookAhead() {

    override fun updateTargetTile(world: World, ghost: Ghost, targetTile: Position) {
        targetTile.copy(ghost.scatterTargetTile)
    }
}

/**
 * The behaviors are designed to be completely stateless and only manipulate ghost state
 * It make it possible to create a single reusable instance of each, and also prevents object
 * allocation during the game
 */
abstract class GhostBehaviorWithLookAhead : GhostBehaviorMode() {

    abstract fun updateTargetTile(world: World, ghost: Ghost, targetTile: Position)

    override fun onStart(world: World, ghost: Ghost) = with (ghost) {
        // We need to build up our look-ahead parameters, so we start with the current ghost position
        lookAheadPosition.copy(ghost.position)
        lookAheadDirection = getNextDirection(ghost, world) // Find the best direction
        lookAheadPosition.move(lookAheadDirection, limitToMazeBounds = true)
        ghost.nextDirection = lookAheadDirection
    }

    override fun onPositionUpdated(world: World, ghost: Ghost, mazePositionChanged: Boolean) = with (ghost) {
        if (!mazePositionChanged) return

        require(ghost.position.mazeIndex == lookAheadPosition.mazeIndex) { "pos=${ghost.position}, ahead=$lookAheadPosition" }

        ghost.nextDirection = lookAheadDirection

        lookAheadPosition.move(lookAheadDirection, limitToMazeBounds = true)

        lookAheadDirection = getNextDirection(ghost, world)
    }

    private fun getNextDirection(ghost: Ghost, world: World): Direction = with (ghost) {
        val maze = world.maze
        updateTargetTile(world, ghost, targetTile)

        fun Direction.targetDistance(): Int
            = if (this != nextDirection.oppositeDirection && canMove(maze, lookAheadPosition, this))
                lookAheadPosition.sqrDistanceFromDirectionTo(this, targetTile) else Int.MAX_VALUE

        fun Int.smallerOrEqualTo(d1: Int, d2: Int, d3: Int) = this <= d1 && this <= d2 && this <= d3

        // Order matters. If distances in valid directions are equal
        // The ghost prefers direction in order: up, left, down, right
        val d1 = UP.targetDistance()
        val d2 = LEFT.targetDistance()
        val d3 = DOWN.targetDistance()
        val d4 = RIGHT.targetDistance()

        return when {
            d1.smallerOrEqualTo(d2, d3, d4) -> UP
            d2.smallerOrEqualTo(d1, d3, d4) -> LEFT
            d3.smallerOrEqualTo(d1, d2, d4) -> DOWN
            else -> RIGHT
        }
    }
}