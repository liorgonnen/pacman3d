package pacman3d.renderables

import pacman3d.entities.PacManLives
import pacman3d.entities.World
import pacman3d.ext.plusAssign
import pacman3d.logic.Position
import three.js.Group

class PacManLivesRenderable : Renderable {

    private val pacmanObjects = List(PacManLives.MAX_LIVES) { PacManMesh() }

    override val sceneObject = Group().apply { this += pacmanObjects }

    override fun setup(world: World) {
        pacmanObjects.forEachIndexed { i, pacman ->
            val pos = Position(3 + i * 3.0, 35.0)
            pacman.position.set(pos.worldX, PacManMesh.SIZE / 2, pos.worldY)
        }
    }

    fun setLives(lives: Int) {
        pacmanObjects.forEachIndexed { i, pacman -> pacman.visible = i < lives }
    }
}