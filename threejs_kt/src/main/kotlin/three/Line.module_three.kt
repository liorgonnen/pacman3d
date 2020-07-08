@file:JsQualifier("THREE")
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

open external class Line : Object3D {
    constructor(geometry: Geometry = definedExternally, material: Material = definedExternally, mode: Number = definedExternally)
    constructor(geometry: Geometry = definedExternally, material: Array<Material> = definedExternally, mode: Number = definedExternally)
    constructor(geometry: BufferGeometry = definedExternally, material: Material = definedExternally, mode: Number = definedExternally)
    constructor(geometry: BufferGeometry = definedExternally, material: Array<Material> = definedExternally, mode: Number = definedExternally)
    open var geometry: dynamic /* Geometry | BufferGeometry */
    open var material: dynamic /* Material | Array<Material> */
    override var type: String /* 'Line' | 'LineLoop' | 'LineSegments' */
    open var isLine: Boolean
    open fun computeLineDistances(): Line /* this */
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
}