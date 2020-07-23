package pacman3d.state

import pacman3d.gameobjects.DotsRenderable
import pacman3d.logic.Position
import pacman3d.state.Maze.Companion.DOT
import pacman3d.state.Maze.Companion.PILL

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