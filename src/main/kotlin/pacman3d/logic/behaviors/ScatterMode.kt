package pacman3d.logic.behaviors

import pacman3d.logic.ActorPosition
import pacman3d.logic.Direction
import pacman3d.state.GameState
import pacman3d.state.GhostState

class ScatterMode : GhostBehaviorMode() {

    /**
     * Ghosts are always thinking one step into the future as they move through the maze. Whenever a ghost enters a new
     * tile, it looks ahead to the next tile along its current direction of travel and decides which way it will go when
     * it gets there. When it eventually reaches that tile, it will change its direction of travel to whatever it had
     * decided on a tile beforehand. The process is then repeated, looking ahead into the next tile along its new
     * direction of travel and making its next decision on which way to go.
     */
    private var lookAheadDirection: Direction = Direction.LEFT
    private val lookAheadPosition = ActorPosition()

    override fun onStart(game: GameState, ghost: GhostState) {
        ghost.requestedDirection = lookAheadDirection
        lookAheadPosition.copy(ghost.position).move(ghost.requestedDirection)
        lookAheadDirection = getNextDirection(ghost, game)
    }

    override fun onPositionUpdated(game: GameState, ghost: GhostState, mazePositionChanged: Boolean) {
        if (mazePositionChanged) {
            require(ghost.position.mazeIndex == lookAheadPosition.mazeIndex) {
                "id=${ghost.id}, pos=${ghost.position}, ahead=$lookAheadPosition"
            }
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
        val d1 = Direction.UP.targetDistance()
        val d2 = Direction.LEFT.targetDistance()
        val d3 = Direction.DOWN.targetDistance()
        val d4 = Direction.RIGHT.targetDistance()

        return when {
            d1.smallerOrEqualTo(d2, d3, d4) -> Direction.UP
            d2.smallerOrEqualTo(d1, d3, d4) -> Direction.LEFT
            d3.smallerOrEqualTo(d1, d2, d4) -> Direction.DOWN
            else -> Direction.RIGHT
        }
    }
}