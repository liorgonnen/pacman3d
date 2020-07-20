package pacman3d.state

import pacman3d.logic.ActorPosition
import pacman3d.logic.Direction

class PacmanState(val maze: MazeState, val initialPosition: ActorPosition) : ActorState(initialPosition) {

    companion object {

        // Cornering is the technique of moving the joystick in the direction one wishes to go well before reaching the
        // center of a turn, ensuring Pac-Man will take the turn as quickly as possible.
        private const val CORNERING_THRESHOLD = 0.2 // Must be between 0 - SUBSTEP_MAX
    }

    internal fun requestDirection(newDirection: Direction) {
        requestedDirection = newDirection

        // When the player is pressing the arrow key in real time, we allow them to do pre-turns
        // and post-turns
        oneShotTurnThreshold = CORNERING_THRESHOLD
    }
}