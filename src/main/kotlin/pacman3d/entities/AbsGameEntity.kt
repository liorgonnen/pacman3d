package pacman3d.entities

import pacman3d.renderables.Renderable

abstract class AbsGameEntity : GameEntity {

    override lateinit var renderable: Renderable

    var isActive = true

    var isVisible = true
        set(value) {
            field = value
            renderable.sceneObject.visible = value
        }

    override fun setup(world: World) {
        renderable = createRenderable().apply { setup(world) }
    }

    open fun resetState(world: World) = Unit
}