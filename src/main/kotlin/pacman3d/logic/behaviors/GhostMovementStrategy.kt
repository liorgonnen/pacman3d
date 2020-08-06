package pacman3d.logic.behaviors

import pacman3d.entities.World
import pacman3d.entities.Ghost
import pacman3d.entities.Maze.Companion.isValid
import pacman3d.ext.sqr
import pacman3d.logic.Direction
import pacman3d.logic.Direction.*
import pacman3d.logic.Position

/**
 * The movement strategies  are designed to be completely stateless and only manipulate ghost state
 * This way, it's possible to create a single reusable instance of each, and also prevents object
 * allocation during the game.
 */
interface GhostMovementStrategy {

    fun onPositionUpdated(world: World, ghost: Ghost, mazePositionChanged: Boolean) = Unit

    fun hasReachedTarget(ghost: Ghost) = false
}

/**
 * Ghosts are always thinking one step into the future as they move through the maze. Whenever a ghost enters a new
 * tile, it looks ahead to the next tile along its current direction of travel and decides which way it will go when
 * it gets there. When it eventually reaches that tile, it will change its direction of travel to whatever it had
 * decided on a tile beforehand. The process is then repeated, looking ahead into the next tile along its new
 * direction of travel and making its next decision on which way to go.
 */
abstract class GhostMovementStrategyWithLookAhead : GhostMovementStrategy {

    abstract fun updateTargetTile(world: World, ghost: Ghost, targetTile: Position)

    override fun onPositionUpdated(world: World, ghost: Ghost, mazePositionChanged: Boolean) = with (ghost) {

        // Switched to a trivial implementation with no look-ahead.
        // TODO: Reimplement look-ahead.
        if (world.maze.isIntersection(position)) {
            updateTargetTile(world, ghost, targetTile)
            nextDirection = findBestDirection(position.mazeX, position.mazeY, currentDirection, ghost, world)
        }
    }

    private fun findBestDirection(
            fromX: Int,
            fromY: Int,
            currentDirection: Direction,
            ghost: Ghost,
            world: World): Direction = with (ghost) {

        fun Direction.targetTileDistance(): Int {
            val toX = fromX + this.x
            val toY = fromY + this.y
            if (this == currentDirection.oppositeDirection || !isLegalMove(world.maze, fromX, fromY, toX, toY)) {
                return Int.MAX_VALUE
            }

            return (fromX - targetTile.mazeX + this.x).sqr + (fromY - targetTile.mazeY + this.y).sqr
        }

        fun Int.smallerOrEqualTo(d1: Int, d2: Int, d3: Int) = this <= d1 && this <= d2 && this <= d3

        // Order matters. If distances in valid directions are equal
        // The ghost prefers direction in order: up, left, down, right
        val d1 = UP.targetTileDistance()
        val d2 = LEFT.targetTileDistance()
        val d3 = DOWN.targetTileDistance()
        val d4 = RIGHT.targetTileDistance()

        require(!(d1 == Int.MAX_VALUE && d1 == d2 && d2 == d3 && d3 == d4)) { "$name, $position: No preferred way" }

        return when {
            d1.smallerOrEqualTo(d2, d3, d4) -> UP
            d2.smallerOrEqualTo(d1, d3, d4) -> LEFT
            d3.smallerOrEqualTo(d1, d2, d4) -> DOWN
            else -> RIGHT
        }
    }
}