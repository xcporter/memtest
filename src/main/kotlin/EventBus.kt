import tornadofx.FXEvent

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

class ViewChange(val state: ViewState) : FXEvent()