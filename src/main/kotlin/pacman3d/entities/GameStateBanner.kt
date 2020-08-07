package pacman3d.entities

import pacman3d.renderables.GameStateBannerRenderer

class GameStateBanner : AbsGameEntity<GameStateBannerRenderer>() {

    companion object {
        internal const val NONE = -1
        const val READY = 0
        const val GAME_OVER = 1
    }

    init {
        isActive = false // This is a static object, no need to call update
    }

    override fun createRenderable() = GameStateBannerRenderer()

    fun show(banner: Int) = renderable.show(banner)

    fun hide() = renderable.show(NONE)
}