package pacman3d.entities

import pacman3d.renderables.DotsRenderable
import pacman3d.logic.Position
import pacman3d.entities.Maze.Companion.DOT
import pacman3d.entities.Maze.Companion.PILL

/**
 * Notes:
 * There are 244 dots overall.
 * 240 small dots x 10 points each
 * 4 energizer dots x 50 points each
 */
class Dots : AbsGameEntity() {

    /**
     * Holds the maze index of the dot or pill that's been eaten in the last update or null otherwise
     */
    var lastEatenIndex: Int? = null
        private set

    override fun createRenderable() = DotsRenderable(this)

    override fun update(world: World, time: Double) = Unit

    override fun onBeforeUpdate() {
        lastEatenIndex = null
    }

    fun eat(world: World, position: Position) = with(world) {
        score += valueOf(maze[position])
        maze.eatDot(position)

        lastEatenIndex = position.mazeIndex
    }

    private fun valueOf(mazeValue: Byte) = when (mazeValue) {
        DOT -> 10
        PILL -> 50
        else -> 0
    }
}