package pacman3d.logic

import pacman3d.logic.Direction.*
import pacman3d.state.GameState
import pacman3d.state.GhostState

abstract class GhostBehaviorMode {

    open val initialDirection: Direction = LEFT

    open fun onPositionUpdated(game: GameState, ghost: GhostState, mazePositionChanged: Boolean) = Unit
    open fun onStart(game: GameState, ghost: GhostState) = Unit
}

object InGhostHouse: GhostBehaviorMode() {

    override val initialDirection = UP

    override fun onPositionUpdated(game: GameState, ghost: GhostState, mazePositionChanged: Boolean) = with (ghost) {
        when {
            position.y <= 16.5 -> requestedDirection = DOWN
            position.y >= 18.0 -> requestedDirection = UP
        }
    }
}

object LeaveGhostHouse: GhostBehaviorMode() {

    override fun onPositionUpdated(game: GameState, ghost: GhostState, mazePositionChanged: Boolean) = with (ghost) {
        when {
            position.mazeY == 14 -> ghost.setMode(ScatterMode, game)
            position.mazeX < 13 -> requestedDirection = RIGHT
            position.mazeX > 13 -> requestedDirection = LEFT
            position.mazeX == 13 -> requestedDirection = UP
        }
    }
}

object ScatterMode : GhostBehaviorMode() {

    override val initialDirection: Direction = LEFT

    /**
     * Ghosts are always thinking one step into the future as they move through the maze. Whenever a ghost enters a new
     * tile, it looks ahead to the next tile along its current direction of travel and decides which way it will go when
     * it gets there. When it eventually reaches that tile, it will change its direction of travel to whatever it had
     * decided on a tile beforehand. The process is then repeated, looking ahead into the next tile along its new
     * direction of travel and making its next decision on which way to go.
     */
    private var lookAheadDirection: Direction = initialDirection
    private val lookAheadPosition = ActorPosition()

    override fun onStart(game: GameState, ghost: GhostState) {
        lookAheadPosition.copy(ghost.position).move(lookAheadDirection)
        lookAheadDirection = getNextDirection(ghost, game)
        ghost.requestedDirection = initialDirection
    }

    override fun onPositionUpdated(game: GameState, ghost: GhostState, mazePositionChanged: Boolean) {
        if (mazePositionChanged) {
            require(ghost.position.mazeIndex == lookAheadPosition.mazeIndex)
            ghost.requestedDirection = lookAheadDirection

            lookAheadPosition.move(lookAheadDirection)

            lookAheadDirection = getNextDirection(ghost, game)
        }
    }

    private fun getNextDirection(ghost: GhostState, game: GameState): Direction = with (ghost) {
        val maze = game.maze

        fun Direction.targetDistance(): Int {
            // Normally, a ghost should not flip its direction
            if (this == requestedDirection.oppositeDirection || !maze.isTileValidInDirection(ghost.type, lookAheadPosition, this)) {
                return Int.MAX_VALUE
            }

            return lookAheadPosition.sqrDistanceFromDirectionTo(this, scatterTargetTile)
        }

        fun Int.smallerOrEqualTo(d1: Int, d2: Int, d3: Int) = this <= d1 && this <= d2 && this <= d3

        // Order matters. If distances in valid directions are equal
        // The ghost prefers direction in order: up, left, down, right
        val d1 = UP.targetDistance()
        val d2 = LEFT.targetDistance()
        val d3 = Direction.DOWN.targetDistance()
        val d4 = RIGHT.targetDistance()

        return when {
            d1.smallerOrEqualTo(d2, d3, d4) -> UP
            d2.smallerOrEqualTo(d1, d3, d4) -> LEFT
            d3.smallerOrEqualTo(d1, d2, d4) -> Direction.DOWN
            else -> RIGHT
        }
    }
}

//class ChaseMode : GhostBehaviorMode() {
//
//}

//class FrightenedMode: GhostBehaviorMode() {
//
//}