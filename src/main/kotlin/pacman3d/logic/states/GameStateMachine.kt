package pacman3d.logic.states

import kotlinx.browser.document
import org.w3c.dom.events.KeyboardEvent
import pacman3d.entities.World

class GameStateMachine(val world: World) {

    private var previousState: GameState? = null

    var currentState: GameState = WaitingForPlayer
        set(value) {
            if (value == field) return
            previousState = field
            field.onLeaveState(world)
            field = value
            field.onEnterState(world)
        }

    init {
        currentState.onEnterState(world)
    }

    private fun handleKeyboardEvent(event: KeyboardEvent) = currentState.handleKeyboardEvent(this, world, event)

    fun start() {
        document.onkeydown = ::handleKeyboardEvent
    }
}