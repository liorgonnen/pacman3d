package pacman3d.entities

import pacman3d.renderables.Renderable

interface GameEntity : Updatable {

    var renderable: Renderable

    fun createRenderable(): Renderable
}