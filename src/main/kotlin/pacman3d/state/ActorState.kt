package pacman3d.state

import pacman3d.logic.ActorPosition
import pacman3d.logic.ActorType
import pacman3d.logic.Direction
import pacman3d.logic.Direction.DOWN
import pacman3d.state.MazeState.Companion.isValid

abstract class ActorState(val type: ActorType, initialPosition: ActorPosition) {

    companion object {
        protected const val DEFAULT_TURN_THRESHOLD = 0.01
    }

    protected var oneShotTurnThreshold = DEFAULT_TURN_THRESHOLD

    val position = initialPosition

    var direction: Direction = DOWN
        private set

    var requestedDirection: Direction = direction

    var speed = 5.0
        protected set

    fun init(game: GameState) {
        reset(game)

        direction = requestedDirection

        if (game.maze.isTileValidInDirection(type, position, direction)) position.correctPosition(direction)
    }

    protected open fun reset(gameState: GameState) = Unit

    fun update(gameState: GameState, time: Double) {

        val preMoveMazeIndex = position.mazeIndex

        updatePosition(gameState.maze, time)
        updateDirection(gameState.maze)

        onPositionUpdated(gameState, time, position.mazeIndex != preMoveMazeIndex)
    }

    protected open fun onPositionUpdated(game: GameState, time: Double, mazePositionChanged: Boolean) = Unit

    private fun updateDirection(maze: MazeState) {
        val turnThreshold = oneShotTurnThreshold
        oneShotTurnThreshold = DEFAULT_TURN_THRESHOLD

        if (requestedDirection != direction &&
            maze.isAllowedToTurn(type, position, requestedDirection, turnThreshold)) direction = requestedDirection
    }

    // TODO: Can probably simplofy this method some more
    private fun updatePosition(maze: MazeState, time: Double): Unit = with (position) {

        // Check if a movement in the current direction will overshoot and prevent us from turning
        // to the lateral axis
        fun isOvershooting(valueBefore: Double, delta: Double): Boolean
            = minOf(valueBefore, valueBefore + delta) < 0.5 && maxOf(valueBefore, valueBefore + delta) > 0.5

        val isChangingMovementAxis = requestedDirection differentDirectionalityThan direction &&
            maze.isTileValidInDirection(type, position, requestedDirection)

        val distance = (speed * time).coerceAtMost(0.5)

        // We add 0.5 because we want to check the position at the edge of the tile
        // Specifically the edge in the direction we're moving towards
        val newX = x + direction.x * (distance + 0.5)
        val newY = y + direction.y * (distance + 0.5)
        val isNextTileValid = maze[newX.toInt(), newY.toInt()].isValid(type)
        val isOvershootingX = isChangingMovementAxis && isOvershooting(x - mazeX, direction.x * distance)
        val isOvershootingY = isChangingMovementAxis && isOvershooting(y - mazeY, direction.y * distance)

        when {
            isOvershootingX -> centerX()
            isOvershootingY -> centerY()
            isNextTileValid -> move(direction, distance)
            else -> correctPosition(direction)
        }
    }
}