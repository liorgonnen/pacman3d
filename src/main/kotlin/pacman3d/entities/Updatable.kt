package pacman3d.entities

interface Updatable {

    fun setup(world: World)

    fun update(world: World, time: Double)
}