package pacman3d.entities

import pacman3d.renderables.GameEntity
import pacman3d.renderables.Renderable

abstract class AbsGameEntity : GameEntity {

    override lateinit var renderable: Renderable

    override fun setup(world: World) {
        renderable = createRenderable().apply { setup(world) }
    }

    override fun onBeforeUpdate() = Unit
}