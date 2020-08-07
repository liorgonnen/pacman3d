package pacman3d.entities

import pacman3d.renderables.PacManLivesRenderable

class PacManLives : AbsGameEntity<PacManLivesRenderable>() {

    companion object {
        const val MAX_LIVES = 3
    }

    override fun createRenderable() = PacManLivesRenderable()

    fun setLives(lives: Int) = renderable.setLives(lives)
}