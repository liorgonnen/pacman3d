package pacman3d.state

import pacman3d.gameobjects.GameEntity
import pacman3d.gameobjects.Renderable

abstract class AbsGameEntity : GameEntity {

    override lateinit var renderable: Renderable

    override fun setup(world: World) {
        renderable = createRenderable().apply { setup(world) }
    }

    override fun onBeforeUpdate() = Unit
}