package pacman3d.state

import pacman3d.logic.*

class GhostState(
        val id: GhostId,
        initialPosition: ActorPosition,
        val scatterTargetTile: ActorPosition
) : ActorState(initialPosition) {

    private val mode: GhostBehaviorMode = when (id) {
        GhostId.Blinky -> ScatterMode()
        GhostId.Inky -> LeaveGhostHouse()
        else -> InGhostHouse()
    }

    override fun reset(gameState: GameState) {
        super.reset(gameState)

        requestedDirection = mode.initialDirection
        mode.onStart(gameState, this)
    }

    override fun onPositionUpdated(game: GameState, time: Double, mazePositionChanged: Boolean) {
        mode.onPositionUpdated(game, this, mazePositionChanged)
    }
}