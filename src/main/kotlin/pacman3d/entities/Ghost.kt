package pacman3d.entities

import pacman3d.entities.Maze.Companion.EMPTY
import pacman3d.entities.Maze.Companion.GHOST_HOUSE
import pacman3d.entities.Maze.Companion.isGhostHouse
import pacman3d.entities.Maze.Companion.isValid
import pacman3d.ext.truncate
import pacman3d.renderables.GhostRenderable
import pacman3d.logic.*
import pacman3d.logic.GhostState.Eaten
import pacman3d.logic.GhostState.Frightened
import pacman3d.logic.behaviors.*

class DotCounter(var count: Int = 0, var limit: Int = 0) {

    val reachedLimit get() = count >= limit

    fun reset() { count = 0 }
}

abstract class Ghost(
    val name: String,
    private val color: Int,
    val initialState: GhostState,
    initialPosition: Position,
    initialDirection: Direction,
    val scatterTargetTile: Position
) : MovableGameEntity<GhostRenderable>(initialPosition, initialDirection) {

    var movementStrategyStep = 0

    val dotCounter = DotCounter()

    val targetTile = Position()

    var mandatoryDirectionOverride: Direction? = null

    var state: GhostState = initialState
        set(value) {
            val previousState = state
            field = value
            movementStrategy = value.movementStrategy
            renderable.onStateChanged(value, previousState)
        }

    private var movementStrategy: GhostMovementStrategy = state.movementStrategy
        set(value) {
            field = value
            nextDirection = currentDirection
            movementStrategyStep = 0
        }

    val hasReachedTarget get() = movementStrategy.hasReachedTarget(this)

    override fun resetState(world: World) {
        super.resetState(world)

        dotCounter.reset()

        state = initialState
    }

    abstract fun getChaseTargetTile(world: World, targetTile: Position)

    fun reverseDirection() {
        mandatoryDirectionOverride = currentDirection.oppositeDirection
    }

    override fun onPositionUpdated(world: World, time: Double, mazePositionChanged: Boolean) {
        // If the ghost is forced to change direction by the system, we don't need to call the movement strategy
        mandatoryDirectionOverride?.let { newDirection ->
            mandatoryDirectionOverride = null
            nextDirection = newDirection
        } ?: movementStrategy.onPositionUpdated(world, this, mazePositionChanged)
    }

    // TODO: Implement zones where the ghosts cannot turn upward (these are ignored in Frightened mode)
    override fun isLegalMove(maze: Maze, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        require(maze[fromX, fromY].isValid) { "isLegalMove called with invalid position: $fromX, $fromY" }

        val currentMazeValue = maze[fromX, fromY]
        val nextMazeValue = maze[toX, toY]

        // Ghosts can only re-enter the ghost house when eaten
        if (currentMazeValue == EMPTY && nextMazeValue.isGhostHouse) return state == Eaten

        return nextMazeValue.isValid
    }

    override fun createRenderable() = GhostRenderable(this, color)

    override fun toString() = name
}