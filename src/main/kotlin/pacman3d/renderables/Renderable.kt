package pacman3d.renderables

import pacman3d.entities.Updatable
import three.js.Object3D

interface Renderable: Updatable {

    val sceneObject: Object3D
}
