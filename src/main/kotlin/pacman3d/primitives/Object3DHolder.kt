package pacman3d.primitives

import three.js.Object3D

typealias ChangeCallback = (Object3D) -> Unit

abstract class Object3DHolder(private val onChange: ChangeCallback? = null) {
    abstract val sceneObject: Object3D

    protected fun notifySceneObjectUpdated() = onChange?.invoke(sceneObject)
}