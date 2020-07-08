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

external interface `T$19` {
    @nativeGetter
    operator fun get(key: String): Number?
    @nativeSetter
    operator fun set(key: String, value: Number)
}

open external class Mesh : Object3D {
    constructor(geometry: Geometry = definedExternally, material: Material = definedExternally)
    constructor(geometry: Geometry = definedExternally, material: Array<Material> = definedExternally)
    constructor(geometry: BufferGeometry = definedExternally, material: Material = definedExternally)
    constructor(geometry: BufferGeometry = definedExternally, material: Array<Material> = definedExternally)
    open var geometry: dynamic /* Geometry | BufferGeometry */
    open var material: dynamic /* Material | Array<Material> */
    open var morphTargetInfluences: Array<Number>
    open var morphTargetDictionary: `T$19`
    open var isMesh: Boolean
    override var type: String
    open fun updateMorphTargets()
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
}