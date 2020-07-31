package pacman3d.entities

import pacman3d.renderables.Renderable

interface GameEntity<R : Renderable> : Updatable {

    var renderable: R

    fun createRenderable(): R
}