package pacman3d.ext

import pacman3d.primitives.GameObject
import three.js.Object3D

inline operator fun Object3D.plusAssign(child: Object3D) { add(child) }

inline fun Object3D.add(gameObject: GameObject) = apply { add(gameObject.sceneObject) }

inline fun Object3D.add(vararg gameObjects: GameObject) = apply { gameObjects.forEach { add(it) } }

inline operator fun Object3D.plusAssign(gameObject: GameObject) { add(gameObject) }
