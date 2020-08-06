package pacman3d.entities

import pacman3d.entities.Maze.Companion.isGhostHouse
import pacman3d.entities.Maze.Companion.isValid
import pacman3d.renderables.PacManRenderable
import pacman3d.logic.Position
import pacman3d.logic.Direction

class PacMan(
        initialPosition: Position,
        initialDirection: Direction
) : MovableGameEntity<PacManRenderable>(initialPosition, initialDirection) {

    companion object {
        // Cornering is the technique of moving the joystick in the direction one wishes to go well before reaching the
        // center of a turn, ensuring Pac-Man will take the turn as quickly as possible.
        private const val CORNERING_THRESHOLD = 0.1 // Must be between 0 - 0.5
    }

    internal fun requestDirection(newDirection: Direction) {
        nextDirection = newDirection

        // When the player is pressing the arrow key in real time, we allow them to do pre-turns
        // and post-turns
        oneShotTurnThreshold = CORNERING_THRESHOLD
    }

    override fun isLegalMove(maze: Maze, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        val mazeValue = maze[toX, toY]
        return mazeValue.isValid && !mazeValue.isGhostHouse
    }

    override fun createRenderable() = PacManRenderable(this)
}