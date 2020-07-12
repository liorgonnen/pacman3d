package pacman3d.dots

import pacman3d.ext.toMeshPhongMaterial
import pacman3d.primitives.GameObject
import pacman3d.state.GameState
import three.js.BoxGeometry
import three.js.Group
import three.js.InstancedMesh

class Dots : GameObject() {

    companion object {
        const val MAX_COUNT = 244
    }

    private val dotGeometry = BoxGeometry(0.5, 0.5, 0.5)

    private val dotMaterial = 0xffffff.toMeshPhongMaterial()

    override val sceneObject = Group()

    override fun setup(state: GameState) {
        InstancedMesh(dotGeometry, dotMaterial, MAX_COUNT)
    }

    override fun update(state: GameState) {

    }
}