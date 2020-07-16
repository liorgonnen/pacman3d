package pacman3d.ext

import three.js.ParametricGeometry
import three.js.Vector3

typealias ParametricFunc = (Double, Double, Vector3) -> Unit

fun parametricGeometry(slices: Int, stacks: Int, func: ParametricFunc) = ParametricGeometry(
        { u, v, dest -> func(u.toDouble(), v.toDouble(), dest) },
        slices,
        stacks
)