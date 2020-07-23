package pacman3d.entities

import pacman3d.renderables.GhostRenderable
import pacman3d.logic.*
import pacman3d.logic.behaviors.GhostBehaviorMode
import pacman3d.logic.behaviors.LeaveGhostHouse
import pacman3d.logic.behaviors.ScatterMode

class Ghost(
        val id: GhostId,
        private val color: Int,
        initialPosition: Position,
        initialDirection: Direction,
        val scatterTargetTile: Position
) : MovableGameEntity(initialPosition, initialDirection) {

    private lateinit var mode: GhostBehaviorMode

    override fun setup(world: World) {
        super.setup(world)

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

    override fun canMove(maze: Maze, fromPosition: Position, xDir: Double, yDir: Double): Boolean {
        val currentMazeValue = maze[fromPosition]
        val nextMazeValue = maze[(fromPosition.x + xDir).toInt(), (fromPosition.y + yDir).toInt()]

        // Cannot re-enter the ghost house for now
        if (currentMazeValue == Maze.EMPTY && nextMazeValue == Maze.GHOST_HOUSE) return false

        return nextMazeValue != Maze.INVALID
    }

    override fun createRenderable() = GhostRenderable(this, color)
}