package ddexamples

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.shape.Rectangle
import tornadofx.*

class SimpleDrag : View() {
    var scoreInt: Int = 0
    var source: Pane by singleAssign()
    var inFlight: Rectangle by singleAssign()
    var view: BorderPane by singleAssign()
    var score: Label by singleAssign()
    var target: Rectangle by singleAssign()
    var adrag: Boolean = false
    val afterDrag = SimpleBooleanProperty(adrag)
    var workarea: Pane by singleAssign()
    override val root = stackpane {
        view = borderpane {
            addClass(Style.background)
            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS

            top {
                vbox(10.0) {
                    text("Drag rectangle into area:") {
                        alignment = Pos.CENTER
                    }
                    hbox(10.0) {
                        alignment = Pos.CENTER
                        maxWidth = 200.0
                        target = rectangle {
                            alignment = Pos.CENTER
                            height = 55.0
                            width = 200.0
                            arcWidth = 10.0
                            arcHeight = 10.0
                            fill = c("#00000000")
                        }
                        add(target)
                        style {
                            borderColor += box(c(EfasColors.GREY.primary))
                            borderWidth += box(5.px)
                            borderStyle += BorderStrokeStyle.DASHED
                            borderRadius += box(15.px)
                        }
                    }
                    style {
                        padding = box(20.px)
                    }
                }
            }

            center {
                label("Score") {
                    style {
                        fontSize = 20.px
                    }
                }
                score = label("$scoreInt") {
                    style {
                        fontSize = 20.px
                    }
                }
            }

            bottom {
                source = stackpane {
                    alignment = Pos.BOTTOM_CENTER
                    add(rect())
                    label("Take One") {
                        style{
                            fontSize = 20.px
                            textFill = c(EfasColors.BLUE.shadow)
                            padding = box(18.px)
                        }
                    }
                    style {
                        padding = box(20.px)
                    }
                }
            }
        }

        workarea = pane {
            isMouseTransparent = true
            vgrow = Priority.ALWAYS
            inFlight = rect().apply {
                fill = c(EfasColors.GREEN.primary)
                isVisible = false
            }

            add(inFlight)
        }

        addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
        addEventFilter(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
        addEventFilter(MouseEvent.MOUSE_RELEASED, ::stopDrag)
        addEventFilter(MouseEvent.MOUSE_EXITED, ::stopDrag)
    }

    fun startDrag (e: MouseEvent) {
        val mousePt = source.sceneToLocal( e.sceneX, e.sceneY )
        val spacePt = workarea.sceneToLocal(e.sceneX, e.sceneY)
        if (source.contains(mousePt)) {
            inFlight.toFront()
            val widthOffset = inFlight.widthProperty().value/2
            val heightOffset = inFlight.heightProperty().value/2
            inFlight.layoutXProperty().value = spacePt.x - widthOffset
            inFlight.layoutYProperty().value = spacePt.y - heightOffset
            inFlight.isVisible = true
        }
    }

    fun animateDrag (e: MouseEvent) {
        val mousePt = root.sceneToLocal( e.sceneX, e.sceneY )
        afterDrag.value = true
        if (view.contains(mousePt)) {
            val widthOffset = inFlight.widthProperty().value/2
            val heightOffset = inFlight.heightProperty().value/2
            inFlight.relocate(mousePt.x - widthOffset, mousePt.y - heightOffset)
        }
    }

    fun stopDrag (e: MouseEvent) {
        val mousePt = target.sceneToLocal( e.sceneX, e.sceneY )
        if (target.contains(mousePt) && afterDrag.value == true && inFlight.isVisible) {
            scoreInt++
            score.text = scoreInt.toString()
            afterDrag.value = false
        }
        inFlight.isVisible = false
    }

    fun rect () : Rectangle {
        val tmp = Rectangle()
        with(tmp) {
            width = 200.0
            height = 60.0
            arcHeight = 10.0
            arcWidth = 10.0
            fill = c(EfasColors.BLUE.primary)
        }
        return tmp
    }
}