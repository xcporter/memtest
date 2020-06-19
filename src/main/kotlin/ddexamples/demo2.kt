package ddexamples

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.shape.Rectangle
import tornadofx.*
import java.util.*

class BucketSort : View() {
    val rand = Random()
    var source: Pane by singleAssign()
    var inFlight: Pane by singleAssign()
    var dest1: HBox by singleAssign()
    var dest2: HBox by singleAssign()
    var view: BorderPane by singleAssign()
    var score: Label by singleAssign()
    val targets = mutableListOf<Rectangle>()
    var adrag: Boolean = false
    val afterDrag = SimpleBooleanProperty(adrag)
    var result: Label by singleAssign()
    var workarea: Pane by singleAssign()
    var card: Label by singleAssign()
    var scp = 0
    val scoreProperty = SimpleIntegerProperty(scp)

    init {
        scoreProperty.onChange {
            score.text = it.toString()
        }
    }

    override val root = stackpane {
        view = borderpane {
            addClass(Style.background)
            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS

            top = hbox {
                text("Sort each card into the correct area:") {
                    alignment = Pos.CENTER
                }
                style {
                    padding = box(10.px)
                }
            }

            center {
                vbox(10.0) {
                    alignment = Pos.CENTER
                    dest1 = hbox(10.0) {
                        alignment = Pos.CENTER
                        maxWidth = 200.0
                        prefHeight = 62.0
                        label ("A") {
                            style {
                                fontSize = 20.px
                            }
                        }
                        style {
                            borderColor += box(c(EfasColors.GREY.primary))
                            borderWidth += box(5.px)
                            borderStyle += BorderStrokeStyle.DASHED
                            borderRadius += box(15.px)
                        }
                    }
                    region ()
                    dest2 = hbox(10.0) {
                        alignment = Pos.CENTER
                        maxWidth = 200.0
                        prefHeight = 62.0
                        label ("B") {
                            style {
                                fontSize = 20.px
                            }
                        }
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

            bottom {
                vbox {
                    hbox {
                        alignment = Pos.CENTER
                        score = label {
                            text = "0"
                            style {
                                fontSize = 40.px
                            }
                        }
                    }

                    hbox {
                        alignment = Pos.CENTER
                        result = label {
                            style {
                                fontSize = 40.px
                            }
                        }
                    }

                    source = stackpane {
                        alignment = Pos.BOTTOM_CENTER
                        add(rect())
                        label("Take One") {
                            style {
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
        }

        workarea = pane {
            isMouseTransparent = true
            vgrow = Priority.ALWAYS
            inFlight = stackpane {
                rectangle {
                    width = 200.0
                    height = 60.0
                    arcHeight = 10.0
                    arcWidth = 10.0
                    fill = c(EfasColors.GREEN.highlight)
                    isVisible = false
                }
                card = label {
                    style {
                        fontSize = 20.px
                        textFill = c(EfasColors.BLUE.shadow)
                        padding = box(18.px)
                    }
                }
            }
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
            card.text = if (rand.nextBoolean()) "A" else "B"
            inFlight.toFront()
            val widthOffset = (inFlight.widthProperty().value/2) + 60.0
            val heightOffset = inFlight.heightProperty().value/2
            inFlight.layoutXProperty().value = spacePt.x - widthOffset
            inFlight.layoutYProperty().value = spacePt.y - heightOffset
            inFlight.getChildList()?.forEach { it.isVisible = true }
        }
    }

    fun animateDrag (e: MouseEvent) {
        val mousePt = root.sceneToLocal( e.sceneX, e.sceneY )
        afterDrag.value = true
        if (view.contains(mousePt)) {
            val widthOffset = (inFlight.widthProperty().value/2) + 60.0
            val heightOffset = inFlight.heightProperty().value/2
            inFlight.relocate(mousePt.x - widthOffset, mousePt.y - heightOffset)
        }
    }

    fun stopDrag (e: MouseEvent) {
        val aPt = dest1.sceneToLocal( e.sceneX, e.sceneY )
        val bPt = dest2.sceneToLocal( e.sceneX, e.sceneY)
        if (afterDrag.value == true && inFlight.isVisible) {
            if (dest1.contains(aPt)) {
                if (card.text == "A") {
                    result.text = "✔︎"
                    scoreProperty.value++
                } else result.text = "✘"
            }

            if (dest2.contains(bPt)) {
                if (card.text == "B") {
                    result.text = "✔︎"
                    scoreProperty.value++
                } else result.text = "✘"
            }

//            Evaluate score
            afterDrag.value = false
        }
        result.text = ""
        inFlight.getChildList()?.forEach { it.isVisible = false }
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
