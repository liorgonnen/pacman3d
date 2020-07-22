package pacman3d.state

import pacman3d.logic.ActorPosition
import pacman3d.logic.Direction

abstract class ActorState(
    initialPosition: ActorPosition,
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

    abstract fun canMove(mazeValue: Byte): Boolean

    open fun setup(game: GameState) = Unit

    fun update(gameState: GameState, time: Double) {

        val preMoveMazeIndex = position.mazeIndex

        updatePosition(gameState.maze, time)
        updateDirection(gameState.maze)

        onPositionUpdated(gameState, time, position.mazeIndex != preMoveMazeIndex)
    }

    protected open fun onPositionUpdated(game: GameState, time: Double, mazePositionChanged: Boolean) = Unit

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

        val isChangingMovementAxis = nextDirection differentAxisThan currentDirection
                && isTileValidInDirection(fromPosition = position, toDirection = nextDirection, maze)

        val distance = (speed * time).coerceAtMost(0.5)

        // We add 0.5 because we want to check the position at the edge of the tile
        // Specifically the edge in the direction we're moving towards
        val newX = x + currentDirection.x * (distance + 0.5)
        val newY = y + currentDirection.y * (distance + 0.5)
        val isNextTileValid = canMove(maze[newX.toInt(), newY.toInt()])
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
                result = result && canMove(maze[mazeX, mazeY + direction.y])
                if (f < 0.5 - threshold) result = result && canMove(maze[mazeX - 1, mazeY + direction.y])
                if (f > 0.5 + threshold) result = result && canMove(maze[mazeX + 1, mazeY + direction.y])
            }
            else {
                val f = y - mazeY
                result = result && canMove(maze[mazeX + direction.x, mazeY])
                if (f < 0.5 - threshold) result = result && canMove(maze[mazeX + direction.x, mazeY - 1])
                if (f > 0.5 + threshold) result = result && canMove(maze[mazeX + direction.x, mazeY + 1])
            }
        }

        return result
    }

    fun isTileValidInDirection(fromPosition: ActorPosition, toDirection: Direction, maze: MazeState)
        = canMove(maze[fromPosition.mazeX + toDirection.x, fromPosition.mazeY + toDirection.y])
}