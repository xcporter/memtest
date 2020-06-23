import ddexamples.BucketSort
import ddexamples.ColumnConnect
import ddexamples.Reorder
import ddexamples.SimpleDrag
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.input.MouseEvent.MOUSE_PRESSED
import javafx.scene.layout.Priority
import kfoenix.jfxhamburger
import tornadofx.*

class MainView : View() {
    private val menuButton = jfxhamburger {
        this.children.style {
            backgroundColor += c(EfasColors.MONO.highlight)
        }
        alignment = Pos.CENTER_LEFT
        isVisible = false
    }

    init {
        menuButton.addEventHandler(MOUSE_PRESSED) { fire(ViewChange(ViewState.MENU, scope)) }
        subscribe<ViewChange> {
            when (it.state) {
                ViewState.MENU -> {
                    menuButton.isVisible = false
                    root.center.replaceWith(
                            find<Menu>().root,
                            transition = ViewTransition.Slide(0.5.seconds, ViewTransition.Direction.RIGHT)
                    )
                }
                ViewState.SCREEN1 -> {
                    menuButton.isVisible = true
                    root.center.replaceWith(
                            find<SimpleDrag>().root,
                            transition = ViewTransition.Slide(0.5.seconds, ViewTransition.Direction.LEFT)
                    )
                }
                ViewState.SCREEN2 -> {
                    menuButton.isVisible = true
                    root.center.replaceWith(
                            find<BucketSort>().root,
                            transition = ViewTransition.Slide(0.5.seconds, ViewTransition.Direction.LEFT)
                    )
                }
                ViewState.SCREEN3 -> {
                    menuButton.isVisible = true
                    root.center.replaceWith(
                            find<ColumnConnect>().root,
                            transition = ViewTransition.Slide(0.5.seconds, ViewTransition.Direction.LEFT)
                    )
                }
                ViewState.SCREEN4 -> {
                    menuButton.isVisible = true
                    root.center.replaceWith(
                            find<Reorder>().root,
                            transition = ViewTransition.Slide(0.5.seconds, ViewTransition.Direction.LEFT)
                    )
                }
            }
        }
    }

    override val root = borderpane {
        top = hbox (20.0) {
            addClass(Style.header)
            useMaxWidth = true
            this += menuButton
        }

        center = find<Menu>().root

    }
}

class Menu : View () {
    override val root = borderpane {
        addClass(Style.background)
        top = hbox {
            alignment = Pos.CENTER
            label ("Main Menu")
        }

        center = vbox (20.0)  {
            alignment = Pos.CENTER

            button("Screen 1") {
                action {
                    fire(ViewChange(ViewState.SCREEN1, scope))
                }
            }
            button("Screen 2") {
                action {
                    fire(ViewChange(ViewState.SCREEN2, scope))
                }
            }
            button("Screen 3") {
                action {
                    fire(ViewChange(ViewState.SCREEN3, scope))
                }
            }
            button("Screen 4") {
                action {
                    fire(ViewChange(ViewState.SCREEN4, scope))
                }
            }
            button("quit") {
                action {
                }
            }
        }
    }
}

class EnderZone : View ()  {
    val content = vbox (20.0) {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        alignment = Pos.CENTER
        label("This is the end") {
            alignment = Pos.CENTER
        }
        button("back") {
            alignment = Pos.CENTER
            action {
            }
        }
    }
    init {
        content.addEventHandler(MOUSE_PRESSED) {
            println(scope)
            println(subscribedEvents.toString())
        }
    }
    override val root = content
}