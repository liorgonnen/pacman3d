package pacman3d.state

import pacman3d.logic.*

class GhostState(
        val id: GhostId,
        initialPosition: ActorPosition,
        val scatterTargetTile: ActorPosition
) : ActorState(initialPosition) {

    private val mode: GhostBehaviorMode = if (id == GhostId.Blinky) ScatterMode() else InGhostHouse()

    override fun reset(gameState: GameState) {
        super.reset(gameState)

        direction = mode.initialDirection
    }

    override fun onPositionUpdated(game: GameState, time: Double, mazePositionChanged: Boolean) {
        mode.onPositionUpdated(game, this, time, mazePositionChanged)
    }
}