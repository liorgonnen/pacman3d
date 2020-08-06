package pacman3d.renderables

import pacman3d.entities.Updatable
import pacman3d.entities.World
import three.js.Object3D

interface Renderable: Updatable {

    val sceneObject: Object3D

    override fun update(world: World, time: Double) = Unit
}
