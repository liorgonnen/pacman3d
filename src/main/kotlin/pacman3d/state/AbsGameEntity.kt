package pacman3d.state

import pacman3d.logic.Position
import pacman3d.logic.Direction

abstract class AbsGameEntity(
        initialPosition: Position,
        initialDirection: Direction
) {
    companion object {
        protected const val DEFAULT_TURN_THRESHOLD = 0.01
    }

    protected var oneShotTurnThreshold = DEFAULT_TURN_THRESHOLD

    val position = initialPosition

    var currentDirection = initialDirection
        private set

    var nextDirection = initialDirection

    var speed = 5.0
        protected set

    open fun setup(world: World) = Unit

    fun update(world: World, time: Double) {

        val preMoveMazeIndex = position.mazeIndex

        updatePosition(world.maze, time)
        updateDirection(world.maze)

        onPositionUpdated(world, time, position.mazeIndex != preMoveMazeIndex)
    }

    protected open fun onPositionUpdated(world: World, time: Double, mazePositionChanged: Boolean) = Unit

    private fun updateDirection(maze: MazeState) {
        if (currentDirection != nextDirection && canTurn(maze, nextDirection, oneShotTurnThreshold)) {
            currentDirection = nextDirection
        }

        oneShotTurnThreshold = DEFAULT_TURN_THRESHOLD
    }

    // TODO: Can probably simplify this method some more
    private fun updatePosition(maze: MazeState, time: Double): Unit = with (position) {

        // Check if a movement in the current direction will overshoot and prevent us from turning
        // to the lateral axis
        fun isOvershooting(valueBefore: Double, delta: Double): Boolean
            = minOf(valueBefore, valueBefore + delta) < 0.5 && maxOf(valueBefore, valueBefore + delta) > 0.5

        val isChangingMovementAxis = nextDirection differentAxisThan currentDirection && canMove(maze, position, nextDirection)

        val distance = (speed * time).coerceAtMost(0.5)

        // We add 0.5 because we want to check the position at the edge of the tile
        // Specifically the edge in the direction we're moving towards
        val xDir = currentDirection.x * (distance + 0.5)
        val yDir = currentDirection.y * (distance + 0.5)
        val isNextTileValid = canMove(maze, position, xDir, yDir)
        val isOvershootingX = isChangingMovementAxis && isOvershooting(x - mazeX, currentDirection.x * distance)
        val isOvershootingY = isChangingMovementAxis && isOvershooting(y - mazeY, currentDirection.y * distance)

        when {
            isOvershootingX -> centerX()
            isOvershootingY -> centerY()
            isNextTileValid -> move(currentDirection, distance)
            else -> correctPosition(currentDirection)
        }
    }

    // TODO: Simplify this or clean this up.
    private fun canTurn(maze: MazeState, direction: Direction, threshold: Double): Boolean {
        var result = true
        with(position) {
            if (direction.isVertical) {
                val f = x - mazeX
                result = result && canMove(maze, position, direction)
                if (f < 0.5 - threshold) result = result && canMove(maze, position, -1, direction.y)
                if (f > 0.5 + threshold) result = result && canMove(maze, position,  1, direction.y)
            }
            else {
                val f = y - mazeY
                result = result && canMove(maze, position, direction)
                if (f < 0.5 - threshold) result = result && canMove(maze, position, direction.x, -1)
                if (f > 0.5 + threshold) result = result && canMove(maze, position, direction.x,  1)
            }
        }

        return result
    }

    fun canMove(maze: MazeState, fromPosition: Position, direction: Direction): Boolean
        = canMove(maze, fromPosition, direction.x, direction.y)

    fun canMove(maze: MazeState, fromPosition: Position, xDir: Int, yDir: Int): Boolean
        = canMove(maze, fromPosition, xDir.toDouble(), yDir.toDouble())

    abstract fun canMove(maze: MazeState, fromPosition: Position, xDir: Double, yDir: Double): Boolean
}