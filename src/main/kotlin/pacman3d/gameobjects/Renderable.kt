package pacman3d.gameobjects

import pacman3d.state.World
import three.js.Object3D

interface Updatable {
    fun setup(world: World)

    fun update(world: World, time: Double)
}

interface GameEntity : Updatable {

}

interface Renderable : Updatable {

    val sceneObject: Object3D
}