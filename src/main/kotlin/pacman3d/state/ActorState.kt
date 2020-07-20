package pacman3d.state

import pacman3d.logic.ActorPosition
import pacman3d.logic.Direction
import pacman3d.logic.Direction.DOWN
import pacman3d.state.MazeState.Companion.isValid

abstract class ActorState(initialPosition: ActorPosition) {

    companion object {
        protected const val DEFAULT_TURN_THRESHOLD = 0.1
    }

    protected var oneShotTurnThreshold = DEFAULT_TURN_THRESHOLD

    val position = ActorPosition(initialPosition.x, initialPosition.y)

    var direction: Direction = DOWN

    var requestedDirection: Direction = DOWN

    var speed = 5.0
        protected set

    open fun reset(gameState: GameState) = Unit

    open fun update(gameState: GameState, time: Double) {
        updateDirection(gameState.maze)

        val preMoveMazeIndex = position.mazeIndex

        updatePosition(gameState.maze, time)

        onPositionUpdated(gameState, time, position.mazeIndex != preMoveMazeIndex)
    }

    protected open fun onPositionUpdated(game: GameState, time: Double, mazePositionChanged: Boolean) = Unit

    private fun updateDirection(maze: MazeState) {
        val turnThreshold = oneShotTurnThreshold
        oneShotTurnThreshold = DEFAULT_TURN_THRESHOLD

        if (requestedDirection == direction ||
            !position.allowedToTurn(turnThreshold) ||
            !maze.isTileValidInDirection(position, requestedDirection)) return

        direction = requestedDirection
    }

    private fun updatePosition(maze: MazeState, time: Double): Unit = with (position) {
        val distance = speed * time

        // We add 0.5 because we want to check the position at the edge of the tile
        // Specifically the edge in the direction we're moving towards
        val newX = x + direction.x * (distance + 0.5)
        val newY = y + direction.y * (distance + 0.5)
        if (maze[newX.toInt(), newY.toInt()].isValid) move(direction, distance) else centerInTile()
    }
}