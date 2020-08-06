package pacman3d.entities

import pacman3d.logic.Direction
import pacman3d.logic.Position
import pacman3d.renderables.Renderable

abstract class MovableGameEntity<R : Renderable>(
    private val initialPosition: Position,
    private val initialDirection: Direction
) : AbsGameEntity<R>() {

    companion object {
        protected const val DEFAULT_TURN_THRESHOLD = 0.01
    }

    protected var oneShotTurnThreshold = DEFAULT_TURN_THRESHOLD

    val position = Position()

    var currentDirection = initialDirection
        private set

    var nextDirection = initialDirection

    var speed = 5.0

    override fun resetState(world: World) {
        super.resetState(world)

        position.copy(initialPosition)
        currentDirection = initialDirection
        nextDirection = initialDirection
    }

    override fun update(world: World, time: Double) {
        val preMoveMazeIndex = position.mazeIndex

        updateDirection(world.maze)
        updatePosition(world.maze, time)

        onPositionUpdated(world, time, position.mazeIndex != preMoveMazeIndex)
    }

    protected open fun onPositionUpdated(world: World, time: Double, mazePositionChanged: Boolean) = Unit

    open fun updateDirection(maze: Maze) {
        if (currentDirection != nextDirection && canTurn(maze, nextDirection, oneShotTurnThreshold)) {
            currentDirection = nextDirection
        }

        oneShotTurnThreshold = DEFAULT_TURN_THRESHOLD
    }

    // TODO: Can probably simplify this method some more
    private fun updatePosition(maze: Maze, time: Double): Unit = with (position) {

        // Check if a movement in the current direction will overshoot and prevent us from turning
        // to the lateral axis
        fun isOvershooting(valueBefore: Double, delta: Double): Boolean {
            val valueAfter = valueBefore + delta
            return minOf(valueBefore, valueAfter) < 0.5 && maxOf(valueBefore, valueAfter) > 0.5
        }

        val isChangingMovementAxis = nextDirection differentAxisThan currentDirection && isLegalMove(maze, position, nextDirection)

        val distance = (speed * time).coerceAtMost(0.5)

        // We add 0.5 because we want to check the position at the edge of the tile
        // Specifically the edge in the direction we're moving towards
        val distanceCeil = distance + 0.5
        val isNextTileValid = isLegalMove(maze, position, currentDirection.x * distanceCeil, currentDirection.y * distanceCeil)
        val isOvershootingX = isChangingMovementAxis && isOvershooting(fractionX, currentDirection.x * distance)
        val isOvershootingY = isChangingMovementAxis && isOvershooting(fractionY, currentDirection.y * distance)

        when {
            isOvershootingX -> centerX()
            isOvershootingY -> centerY()
            isNextTileValid -> move(currentDirection, distance, limitToMazeBounds = true)
            else -> correctPosition(currentDirection)
        }
    }

    private fun canTurn(maze: Maze, direction: Direction, threshold: Double): Boolean {
        if (!isLegalMove(maze, position, direction)) return false

        if (direction.isVertical) {
            if (position.fractionX < 0.5 - threshold && !isLegalMove(maze, position, -1, direction.y)) return false
            if (position.fractionX > 0.5 + threshold && !isLegalMove(maze, position,  1, direction.y)) return false
        }
        else {
            if (position.fractionY < 0.5 - threshold && !isLegalMove(maze, position, direction.x, -1)) return false
            if (position.fractionY > 0.5 + threshold && !isLegalMove(maze, position, direction.x,  1)) return false
        }

        return true
    }

    fun isLegalMove(maze: Maze, fromPosition: Position, direction: Direction)
        = isLegalMove(maze, fromPosition, direction.x, direction.y)

    private fun isLegalMove(maze: Maze, fromPosition: Position, xDir: Int, yDir: Int): Boolean
        = isLegalMove(maze, fromPosition.mazeX, fromPosition.mazeY, fromPosition.mazeX + xDir, fromPosition.mazeY + yDir)

    private fun isLegalMove(maze: Maze, fromPosition: Position, xDir: Double, yDir: Double): Boolean
        = isLegalMove(maze, fromPosition.mazeX, fromPosition.mazeY, (fromPosition.x + xDir).toInt(), (fromPosition.y + yDir).toInt())

    abstract fun isLegalMove(maze: Maze, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean
}