package pacman3d.state

import pacman3d.logic.*
import pacman3d.logic.behaviors.GhostBehaviorMode
import pacman3d.logic.behaviors.LeaveGhostHouse
import pacman3d.logic.behaviors.ScatterMode

class GhostState(
        val id: GhostId,
        initialPosition: Position,
        initialDirection: Direction,
        val scatterTargetTile: Position
) : AbsGameEntity(initialPosition, initialDirection) {

    private lateinit var mode: GhostBehaviorMode

    override fun setup(world: World) {
        setMode(when (id) {
            GhostId.Blinky -> ScatterMode()
            //GhostId.Clyde -> LeaveGhostHouse
            else -> LeaveGhostHouse//InGhostHouse
        }, world)
    }

    fun setMode(newMode: GhostBehaviorMode, world: World) {
        mode = newMode.also { it.onStart(world, this) }
    }

    override fun onPositionUpdated(world: World, time: Double, mazePositionChanged: Boolean) {
        mode.onPositionUpdated(world, this, mazePositionChanged)
    }

    override fun canMove(maze: MazeState, fromPosition: Position, xDir: Double, yDir: Double): Boolean {
        val currentMazeValue = maze[fromPosition]
        val nextMazeValue = maze[(fromPosition.x + xDir).toInt(), (fromPosition.y + yDir).toInt()]

        // Cannot re-enter the ghost house for now
        if (currentMazeValue == MazeState.EMPTY && nextMazeValue == MazeState.GHOST_HOUSE) return false

        return nextMazeValue != MazeState.INVALID
    }
}