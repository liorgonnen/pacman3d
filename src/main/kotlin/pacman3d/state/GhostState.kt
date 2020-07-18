package pacman3d.state

import pacman3d.ext.compareTo
import pacman3d.logic.Direction
import pacman3d.logic.Direction.*
import pacman3d.maze.MazeCoordinates


enum class GhostId {
    Blinky,
    Inky,
    Pinky,
    Clyde
}

class GhostState(val id: GhostId, initialPosition: MazeCoordinates) : BaseState(initialPosition) {

    val targetTile = MazeCoordinates(27, 0)

    private var lookAheadDirection: Direction = LEFT
    private val lookAheadPosition = MazeCoordinates()

    private var directionChangePending = false

    override fun reset(gameState: GameState) {
        super.reset(gameState)

        direction = LEFT
        updateLookAheadPosition()
    }

    override fun onMazePositionChanged(gameState: GameState, time: Double) {
        // Make sure we reached where we were planning to go
        require(position == lookAheadPosition)

        directionChangePending = true
    }

    private fun updateLookAheadPosition() {
        lookAheadPosition.copy(position).move(direction)
    }

    private fun findNextDirection(maze: MazeState): Direction {
        if (id == GhostId.Blinky) {

            fun Direction.targetDistance(): Int {
                // Normally, a ghost should not flip its direction
                if (this == direction.oppositeDirection ||
                    !maze.isTileValidInDirection(lookAheadPosition, this)) return Int.MAX_VALUE

                return lookAheadPosition.sqrDistanceFromDirectionTo(this, targetTile)
            }

            fun Int.smallerOrEqualTo(d1: Int, d2: Int, d3: Int) = this <= d1 && this <= d2 && this <= d3

            maze.isTileValidInDirection(position, direction)

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

        // TODO: Remove
        return LEFT
    }

    private fun maybeUpdateDirection(maze: MazeState) {
        val directionChangeAllowed = when (direction) {
            UP -> subStepPosition.y <= SUBSTEP_CENTER
            DOWN -> subStepPosition.y >= SUBSTEP_CENTER
            LEFT -> subStepPosition.x <= SUBSTEP_CENTER
            RIGHT -> subStepPosition.x >= SUBSTEP_CENTER
        }

        if (directionChangeAllowed && directionChangePending) {
            directionChangePending = false
            direction = lookAheadDirection

            updateLookAheadPosition()

            lookAheadDirection = findNextDirection(maze)
        }
    }

    override fun update(gameState: GameState, time: Double) {
        maybeUpdateDirection(gameState.maze)

        super.update(gameState, time)
    }
}