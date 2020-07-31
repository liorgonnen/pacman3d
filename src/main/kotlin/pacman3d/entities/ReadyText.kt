package pacman3d.entities

import pacman3d.renderables.ReadyTextRenderer

class ReadyText : AbsGameEntity<ReadyTextRenderer>() {

    override fun setup(world: World) {
        super.setup(world)

        isActive = false // This is a static object, no need to call update
    }

    override fun createRenderable() = ReadyTextRenderer()

    override fun update(world: World, time: Double) = Unit
}