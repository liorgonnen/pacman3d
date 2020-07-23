package pacman3d.logic.states

import kotlinx.browser.document
import org.w3c.dom.events.KeyboardEvent
import pacman3d.entities.World

class GameStateMachine(val world: World) {

    var currentState: GameState = WaitingForPlayer
        set(value) {
            if (value == field) return

            field.onLeaveState(world)
            field = value
            field.onEnterState(world)
        }

    private fun handleKeyboardEvent(event: KeyboardEvent) = currentState.handleKeyboardEvent(this, world, event)

    fun start() {
        document.onkeydown = ::handleKeyboardEvent
    }
}