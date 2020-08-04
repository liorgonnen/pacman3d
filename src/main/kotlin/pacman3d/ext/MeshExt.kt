package pacman3d.ext

import three.js.Box3
import three.js.BufferGeometry
import three.js.Geometry
import three.js.Mesh

val Mesh.boundingBox: Box3
    get() {
        return when (geometry) {
            is Geometry -> geometry.boundingBox
            is BufferGeometry -> geometry.boundingBox
            else -> null
        } ?: error("No bounding box")
    }

val Box3.width get() = max.x - min.x.toDouble()

val Box3.height get() = max.y - min.y.toDouble()