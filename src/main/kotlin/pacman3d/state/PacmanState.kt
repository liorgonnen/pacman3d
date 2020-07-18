package pacman3d.state

import pacman3d.ext.absValue
import pacman3d.logic.Direction
import pacman3d.logic.Direction.DOWN
import pacman3d.maze.MazeCoordinates

class PacmanState(val maze: MazeState, val initialPosition: MazeCoordinates) : BaseState(initialPosition) {

    companion object {

        // Cornering is the technique of moving the joystick in the direction one wishes to go well before reaching the
        // center of a turn, ensuring Pac-Man will take the turn as quickly as possible.
        private const val CORNERING_THRESHOLD = 0.1 // Must be between 0 - SUBSTEP_MAX
    }

    private var requestedDirection: Direction = DOWN

    private fun isInCorneringThreshold(threshold: Double) =
        (if (direction.isHorizontal) subStepPosition.x else subStepPosition.y).absValue <= threshold

    internal fun requestDirection(newDirection: Direction) {
        requestedDirection = newDirection

        // When the player is pressing the arrow key in real time, we allow them to do pre-turns
        // and post-turns
        maybeUpdateDirection(CORNERING_THRESHOLD)
    }

    private fun maybeUpdateDirection(corneringThreshold: Double) {
        if (requestedDirection == direction) return

        val differentDirectionality = requestedDirection.isHorizontal != direction.isHorizontal

        if (differentDirectionality && !isInCorneringThreshold(corneringThreshold)) return

        if (!maze.isTileValidInDirection(position, requestedDirection)) return

        direction = requestedDirection
    }

    override fun update(gameState: GameState, time: Double) {
        // If the player pressed the arrow keys way ahead of time, we don't allow any cornering
        maybeUpdateDirection(0.1)

        super.update(gameState, time)
    }
}