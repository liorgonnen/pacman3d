package pacman3d.state

import pacman3d.logic.*
import pacman3d.logic.behaviors.GhostBehaviorMode
import pacman3d.logic.behaviors.InGhostHouse
import pacman3d.logic.behaviors.LeaveGhostHouse
import pacman3d.logic.behaviors.ScatterMode

class GhostState(
        val id: GhostId,
        initialPosition: ActorPosition,
        initialDirection: Direction,
        val scatterTargetTile: ActorPosition
) : ActorState(initialPosition, initialDirection) {

    private lateinit var mode: GhostBehaviorMode

    override fun setup(game: GameState) {
        setMode(when (id) {
            GhostId.Blinky -> ScatterMode()
            GhostId.Clyde -> InGhostHouse
            else -> InGhostHouse
        }, game)
    }

    fun setMode(newMode: GhostBehaviorMode, game: GameState) {
        mode = newMode
        mode.onStart(game, this)
    }

    override fun onPositionUpdated(game: GameState, time: Double, mazePositionChanged: Boolean) {
        mode.onPositionUpdated(game, this, mazePositionChanged)
    }

    override fun canMove(mazeValue: Byte) = mazeValue != MazeState.INVALID
}