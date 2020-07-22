package pacman3d.state

import pacman3d.logic.*
import pacman3d.logic.behaviors.GhostBehaviorMode
import pacman3d.logic.behaviors.InGhostHouse
import pacman3d.logic.behaviors.LeaveGhostHouse
import pacman3d.logic.behaviors.ScatterMode

class GhostState(
        val id: GhostId,
        initialPosition: ActorPosition,
        val scatterTargetTile: ActorPosition
) : ActorState(ActorType.Ghost, initialPosition) {

    private var mode: GhostBehaviorMode = when (id) {
        GhostId.Blinky -> ScatterMode()
        else -> LeaveGhostHouse
    }

    fun setMode(newMode: GhostBehaviorMode, game: GameState) {
        mode = newMode
        mode.onStart(game, this)
    }

    override fun reset(gameState: GameState) {
        super.reset(gameState)

        // TODO: Set to the actual desired mode
        setMode(mode, gameState)
    }

    override fun onPositionUpdated(game: GameState, time: Double, mazePositionChanged: Boolean) {
        mode.onPositionUpdated(game, this, mazePositionChanged)
    }
}