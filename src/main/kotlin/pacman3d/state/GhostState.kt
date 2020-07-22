package pacman3d.state

import pacman3d.logic.*

class GhostState(
        val id: GhostId,
        initialPosition: ActorPosition,
        val scatterTargetTile: ActorPosition
) : ActorState(ActorType.Ghost, initialPosition) {

    private var mode: GhostBehaviorMode = when (id) {
        GhostId.Blinky -> ScatterMode
        GhostId.Inky -> InGhostHouse
        else -> InGhostHouse
    }

    fun setMode(newMode: GhostBehaviorMode, game: GameState) {
        mode = newMode
        mode.onStart(game, this)
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