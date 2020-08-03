package pacman3d.entities

import pacman3d.renderables.Renderable
import pacman3d.utils.lazyDelegatedProperty

abstract class AbsGameEntity<R : Renderable> : GameEntity<R> {

    final override lateinit var renderable: R

    var isActive = true

    var isVisible by lazyDelegatedProperty { renderable.sceneObject::visible }

    override fun setup(world: World) {
        renderable = createRenderable().apply { setup(world) }
    }

    open fun resetState(world: World) = Unit
}