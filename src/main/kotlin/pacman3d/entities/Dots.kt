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

    override fun createRenderable() = DotsRenderable(this)

    override fun update(world: World, time: Double) = Unit
}