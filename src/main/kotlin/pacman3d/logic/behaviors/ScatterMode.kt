package pacman3d.logic.behaviors

import pacman3d.logic.Position
import pacman3d.logic.Direction
import pacman3d.logic.Direction.*
import pacman3d.state.World
import pacman3d.state.Ghost

class ScatterMode : GhostBehaviorMode() {

    /**
     * Ghosts are always thinking one step into the future as they move through the maze. Whenever a ghost enters a new
     * tile, it looks ahead to the next tile along its current direction of travel and decides which way it will go when
     * it gets there. When it eventually reaches that tile, it will change its direction of travel to whatever it had
     * decided on a tile beforehand. The process is then repeated, looking ahead into the next tile along its new
     * direction of travel and making its next decision on which way to go.
     */
    private var lookAheadDirection: Direction = LEFT
    private val lookAheadPosition = Position()

    override fun onStart(world: World, ghost: Ghost) {
        // We need to build up our look-ahead parameters, so we start with the current ghost position
        lookAheadPosition.copy(ghost.position)
        lookAheadDirection = getNextDirection(ghost, world) // Find the best direction
        lookAheadPosition.move(lookAheadDirection)
        ghost.nextDirection = lookAheadDirection
    }

    override fun onPositionUpdated(world: World, ghost: Ghost, mazePositionChanged: Boolean) {
        if (!mazePositionChanged) return

        require(ghost.position.mazeIndex == lookAheadPosition.mazeIndex) {
            "id=${ghost.id}, pos=${ghost.position}, ahead=$lookAheadPosition"
        }

        ghost.nextDirection = lookAheadDirection

        lookAheadPosition.move(lookAheadDirection)

        lookAheadDirection = getNextDirection(ghost, world)
    }

    private fun getNextDirection(ghost: Ghost, world: World): Direction = with (ghost) {
        val maze = world.maze

        fun Direction.targetDistance(): Int
            = if (this != nextDirection.oppositeDirection && canMove(maze, lookAheadPosition, this))
                lookAheadPosition.sqrDistanceFromDirectionTo(this, scatterTargetTile) else Int.MAX_VALUE

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