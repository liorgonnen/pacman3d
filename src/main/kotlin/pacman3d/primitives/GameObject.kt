package pacman3d.primitives

import pacman3d.state.GameState
import three.js.Object3D

abstract class GameObject {

    abstract val sceneObject: Object3D

    abstract fun setup(state: GameState)

    abstract fun update(state: GameState)
}