package pacman3d.entities

import pacman3d.renderables.ScoreRenderable
import pacman3d.entities.Maze.Companion.isDot
import pacman3d.entities.Maze.Companion.isDotOrPill
import pacman3d.entities.Maze.Companion.isPill

class Score : AbsGameEntity() {

    var points = 0
        private set

    override fun createRenderable() = ScoreRenderable(this)

    override fun update(world: World, time: Double) = with(world) {

        val tile = maze[pacman.position]
        if (tile.isDotOrPill) {
            if (tile.isDot) points += 10
            if (tile.isPill) points += 50
            dots.eat(world, pacman.position)
        }
    }

    operator fun plusAssign(value: Int) {
        points += value
    }
}