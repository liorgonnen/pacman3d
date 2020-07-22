package pacman3d.ext

import three.js.*

typealias ParametricFunc = (Double, Double, Vector3) -> Unit

/**
 * A method that creates a parametric geometry but moves the lambda to be trailing parameter so that it can
 * be placed outside the parenthesis
 *
 * @param slices
 * @param stacks
 * @param func
 */
fun parametricGeometry(slices: Int, stacks: Int, func: ParametricFunc) = ParametricGeometry(
        { u, v, dest -> func(u.toDouble(), v.toDouble(), dest) },
        slices,
        stacks
)

/**
 * A helper class to be used instead of the [ExtrudeGeometryOptions] interface
 *
 * See: https://threejs.org/docs/#api/en/geometries/ExtrudeGeometry
 */
class ExtrudeOptions(
        curveSegments: Int = 12,
        steps: Int = 1,
        depth: Double = 100.0,
        bevelEnabled: Boolean = true,
        bevelThickness: Double = 6.0,
        bevelSize: Double = bevelThickness - 2.0,
        bevelOffset: Double = 3.0,
        bevelSegments: Int = 3,
        override var extrudePath: Curve<Vector3>? = undefined,
        uvGenerator: UVGenerator? = undefined
) : ExtrudeGeometryOptions {

    override var curveSegments: Number? = curveSegments
    override var steps: Number? = steps
    override var depth: Number? = depth
    override var bevelEnabled: Boolean? = bevelEnabled
    override var bevelThickness: Number? = bevelThickness
    override var bevelSize: Number? = bevelSize
    override var bevelOffset: Number? = bevelOffset
    override var bevelSegments: Number? = bevelSegments
    override var UVGenerator: UVGenerator? = uvGenerator
}

class TextParameters(
        override var font: Font,
        size: Double = 100.0,
        thickness: Double = 50.0
) : TextGeometryParameters {

    override var size: Number? = size
    override var height: Number? = thickness
}

fun textGeometry(text: String, textParameters: TextParameters) = TextGeometry(text, textParameters).apply {
    computeFaceNormals()
    computeFlatVertexNormals()
    computeBoundingBox()
}

// TODO: Can I import constants correctly?
const val FrontSide = 0
const val BackSide = 1
const val DoubleSide = 2

var Material.materialSide: Int
    set(value) { asDynamic()["side"] = value }
    get() = asDynamic()["side"] as Int
