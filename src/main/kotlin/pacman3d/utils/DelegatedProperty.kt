package pacman3d.utils

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

fun <T> delegatedProperty(prop: KMutableProperty0<T>) = DelegatedMutableProperty(prop)

fun <T> lazyDelegatedProperty(accessor: () -> KMutableProperty0<T>) = LazyDelegatedMutableProperty(accessor)

class DelegatedMutableProperty<T>(private val prop: KMutableProperty0<T>) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = prop.get()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) { prop.set(value) }
}

class LazyDelegatedMutableProperty<T>(private val accessor: () -> KMutableProperty0<T>) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = accessor().get()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) { accessor().set(value) }
}