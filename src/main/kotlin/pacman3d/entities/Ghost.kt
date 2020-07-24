package pacman3d.entities

import pacman3d.renderables.GhostRenderable
import pacman3d.logic.*
import pacman3d.logic.behaviors.*

abstract class Ghost(
        private val color: Int,
        initialPosition: Position,
        initialDirection: Direction,
        val scatterTargetTile: Position
) : MovableGameEntity(initialPosition, initialDirection) {

    private lateinit var mode: GhostBehaviorMode

    abstract fun getChaseTargetTile(world: World, targetTile: Position)

    override fun setup(world: World) {
        super.setup(world)

        setMode(when (this) {
            is Blinky -> ScatterMode()//ChaseMode()
            else -> LeaveGhostHouse
        }, world)
    }

    fun setMode(newMode: GhostBehaviorMode, world: World) {
        mode = newMode.also { it.onStart(world, this) }
    }

    override fun onPositionUpdated(world: World, time: Double, mazePositionChanged: Boolean) {
        mode.onPositionUpdated(world, this, mazePositionChanged)
    }

    // TODO: Implement zones where the ghosts cannot turn upward (these are ignored in Frightened mode)
    override fun canMove(maze: Maze, fromPosition: Position, xDir: Double, yDir: Double): Boolean {
        val currentMazeValue = maze[fromPosition]
        val nextMazeValue = maze[(fromPosition.x + xDir).toInt(), (fromPosition.y + yDir).toInt()]

        // Cannot re-enter the ghost house for now
        if (currentMazeValue == Maze.EMPTY && nextMazeValue == Maze.GHOST_HOUSE) return false

        return nextMazeValue != Maze.INVALID
    }

    override fun createRenderable() = GhostRenderable(this, color)
}