package pacman3d.entities

import pacman3d.renderables.GhostRenderable
import pacman3d.logic.*
import pacman3d.logic.behaviors.*

class DotCounter(var count: Int = 0, var limit: Int = 0) {

    val reachedLimit get() = count >= limit

    fun reset() { count = 0 }
}

abstract class Ghost(
        private val color: Int,
        val initialMode: GhostBehaviorMode,
        initialPosition: Position,
        initialDirection: Direction,
        val scatterTargetTile: Position
) : MovableGameEntity(initialPosition, initialDirection) {

    /**
     * Ghosts are always thinking one step into the future as they move through the maze. Whenever a ghost enters a new
     * tile, it looks ahead to the next tile along its current direction of travel and decides which way it will go when
     * it gets there. When it eventually reaches that tile, it will change its direction of travel to whatever it had
     * decided on a tile beforehand. The process is then repeated, looking ahead into the next tile along its new
     * direction of travel and making its next decision on which way to go.
     */
    var lookAheadDirection: Direction = Direction.LEFT

    val lookAheadPosition = Position()

    val dotCounter = DotCounter()

    val targetTile = Position()

    var mode: GhostBehaviorMode = InGhostHouse
        private set

    override fun resetState(world: World) {
        super.resetState(world)

        dotCounter.reset()
        setMode(initialMode, world)
    }

    abstract fun getChaseTargetTile(world: World, targetTile: Position)

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