package pacman3d.entities

import pacman3d.logic.Position
import pacman3d.renderables.BonusPointsRenderable

class BonusPoints : AbsGameEntity<BonusPointsRenderable>() {

    override fun createRenderable() = BonusPointsRenderable()

    override fun setup(world: World) {
        super.setup(world)

        isActive = false
    }

    fun show(points: Int, where: Position) = renderable.show(points, where)

    fun hide() = renderable.hide()
}