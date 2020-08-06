package pacman3d.renderables

import pacman3d.assets.AssetLoader
import pacman3d.entities.GameStateBanner.Companion.NONE
import pacman3d.ext.*
import pacman3d.logic.Position
import pacman3d.maze.MazeConst
import pacman3d.entities.World
import three.js.Group
import three.js.Mesh
import three.js.Object3D

class GameStateBannerRenderer : Renderable {

    companion object {
        private const val COLOR = 0xFFFF00
        private const val SIZE = MazeConst.UNIT_SIZE
        private const val THICKNESS = 1.0
        private const val READY = "READY!"
        private const val GAME_OVER = "GAME OVER"

        // Order should be identical to the enumeration in [GameStateBanner]
        private val BANNERS = arrayOf(READY, GAME_OVER)
    }

    override val sceneObject = Group()

    private lateinit var banners: List<Object3D>

    private var currentActiveBanner = NONE

    override fun setup(world: World) = AssetLoader.onFontLoaded { font ->
        val textParams = TextParameters(font, SIZE, THICKNESS)

        banners = BANNERS.map { createText(it, textParams) }

        sceneObject += banners

        if (currentActiveBanner != NONE) show(currentActiveBanner)
    }

    private fun createText(text: String, textParams: TextParameters) = Mesh(textGeometry(text, textParams), COLOR.toMeshLambertMaterial()).apply {
        val pos = Position(15.0, 20.0)
        position.set(-boundingBox.max.x.toDouble() / 2 + pos.worldX, -boundingBox.min.y.toDouble(), pos.worldY)
        rotateX((-40).toRadians())
        visible = false
    }

    fun show(bannerIndex: Int) {
        if (!::banners.isInitialized) {
            currentActiveBanner = bannerIndex
            return
        }

        if (currentActiveBanner != NONE) banners[currentActiveBanner].visible = false
        currentActiveBanner = bannerIndex
        banners[bannerIndex].visible = true
    }
}