package pacman3d.ext

import pacman3d.renderables.Renderable
import three.js.Object3D

inline operator fun Object3D.plusAssign(child: Object3D) { add(child) }

inline operator fun Object3D.plusAssign(children: Collection<Object3D>) { children.forEach { add(it) } }

inline fun Object3D.add(renderable: Renderable) = apply { add(renderable.sceneObject) }

inline fun Object3D.add(vararg renderables: Renderable) = apply { renderables.forEach { add(it) } }

inline operator fun Object3D.plusAssign(renderable: Renderable) { add(renderable) }
