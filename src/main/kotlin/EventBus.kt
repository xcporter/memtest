import tornadofx.FXEvent
import tornadofx.Scope

enum class ViewState {
    MENU,
    SCREEN1,
    SCREEN2,
    SCREEN3,
    SCREEN4
}

/**
 * Event Bus actions
 */

class ViewChange(val state: ViewState, scope: Scope) : FXEvent(scope = scope)