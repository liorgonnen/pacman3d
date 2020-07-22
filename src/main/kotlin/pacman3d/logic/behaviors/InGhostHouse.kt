package pacman3d.logic.behaviors

import pacman3d.logic.Direction
import pacman3d.state.GameState
import pacman3d.state.GhostState

object InGhostHouse: GhostBehaviorMode() {

    override fun onPositionUpdated(game: GameState, ghost: GhostState, mazePositionChanged: Boolean) = with (ghost) {
        when {
            position.y <= 16.5 -> nextDirection = Direction.DOWN
            position.y >= 18.0 -> nextDirection = Direction.UP
        }
    }

    override fun onStart(game: GameState, ghost: GhostState) {
        ghost.nextDirection = Direction.UP
    }
}