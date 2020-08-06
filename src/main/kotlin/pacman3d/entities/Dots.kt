package pacman3d.entities

import pacman3d.logic.Position
import pacman3d.renderables.DotsRenderable

/**
 * Notes:
 * There are 244 dots overall.
 * 240 small dots x 10 points each
 * 4 energizer dots x 50 points each
 */
class Dots : AbsGameEntity<DotsRenderable>() {

    override fun createRenderable() = DotsRenderable(this)

    fun onDotEaten(position: Position) = renderable.eat(position)
}