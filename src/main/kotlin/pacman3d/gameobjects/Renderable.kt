package pacman3d.gameobjects

import pacman3d.state.World
import three.js.Object3D

interface Updatable {

    fun setup(world: World)

    fun update(world: World, time: Double)
}

interface GameEntity : Updatable {

    var renderable: Renderable

    fun createRenderable(): Renderable

    fun onBeforeUpdate()
}

interface Renderable: Updatable {

    abstract val sceneObject: Object3D
}
