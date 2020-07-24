package pacman3d.entities

import pacman3d.renderables.ScoreRenderable
import pacman3d.entities.Maze.Companion.isDot
import pacman3d.entities.Maze.Companion.isDotOrPill
import pacman3d.entities.Maze.Companion.isPill

class Score : AbsGameEntity() {

    var points = 0
        private set

    override fun createRenderable() = ScoreRenderable(this)

    override fun update(world: World, time: Double) = Unit

    operator fun plusAssign(value: Int) {
        points += value
    }
}