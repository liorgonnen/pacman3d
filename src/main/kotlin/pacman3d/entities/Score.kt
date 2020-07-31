package pacman3d.entities

import pacman3d.renderables.ScoreRenderable

class Score : AbsGameEntity<ScoreRenderable>() {

    var points = 0
        private set

    override fun createRenderable() = ScoreRenderable(this)

    override fun update(world: World, time: Double) = Unit

    operator fun plusAssign(value: Int) {
        points += value
    }
}