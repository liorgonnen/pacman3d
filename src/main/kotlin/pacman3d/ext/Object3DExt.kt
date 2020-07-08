package pacman3d.ext

import pacman3d.primitives.Object3DHolder
import three.js.Object3D

inline operator fun Object3D.plusAssign(child: Object3D) { add(child) }

inline fun Object3D.add(holder: Object3DHolder) = apply { add(holder.sceneObject) }

inline fun Object3D.add(vararg holders: Object3DHolder) = apply { holders.forEach { add(it) } }

inline operator fun Object3D.plusAssign(holder: Object3DHolder) { add(holder) }
