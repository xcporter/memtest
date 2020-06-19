package ddexamples

import EfasColors
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.lang.Thread.sleep

class Reorder : View() {
    var inFlight: StackPane by singleAssign()
    var stage: Pane by singleAssign()
    var pool: Pane by singleAssign()
    var flightBox: Rectangle by singleAssign()
    var flightLabel: Label by singleAssign()
    var feedback: Label by singleAssign()
    val words = "The fat cat jumped over the lazy dog.".split(" ")

    val cards = words
        .map { WordCard(it, EfasColors.BLUE) }
        .shuffled()
        .asObservable()
    val staging = (words.indices)
        .map { StageCard() }
        .asObservable()

    init {
        cards.onChange {
            it.next()
            if (it.wasAdded()) {
                pool.children.addAll(it.addedSubList)
            } else if (it.wasRemoved()) {
                pool.children.removeAll(it.removed)
            }
        }
    }

    override val root = stackpane {
        vbox {
            addClass(Style.background)
            hbox {
                alignment = Pos.CENTER
                text ("Rebuild the sentence with the correct word order: Drag word into position in the staging area") {
                    wrappingWidth = 250.0
                    textAlignment = TextAlignment.CENTER
                }
                style {
                    padding = box(5.px, 0.px)
                }
            }
            stage = flowpane {
                alignment = Pos.CENTER
                prefHeight = 200.0
                hgap = 5.0
                vgap = 10.0
                staging.forEach {
                    add(it)
                }
                style {
                    padding = box (5.px, 5.px)
                }
            }
            hbox {
                alignment = Pos.CENTER
                pool = flowpane {
                    maxWidth = 250.0
                    columnHalignment = HPos.CENTER
                    alignment = Pos.CENTER
                    hgap = 20.0
                    vgap = 10.0
                    cards.forEach {
                        add(it)
                    }
                }
            }
        }
        pane {
            inFlight = stackpane {
                flightBox = rectangle {
                    width = 40.0
                    height = 50.0
                    arcHeight = 10.0
                    arcWidth = 10.0
                    fill = c(EfasColors.GREEN.primary + "8F")
                }
                flightLabel = label {
                    style {
                        fontSize = 20.px
                        textFill = c(EfasColors.GREEN.shadow)
                        padding = box(8.px)
                    }
                }
                isVisible = false
            }
        }
        hbox {
            alignment = Pos.BOTTOM_CENTER
            button {
                text = "check"
                action {
                    val input = stage.children.map {
                        it as StageCard
                        it.label.text
                    }
                    feedback.text = if (input == words) "✔︎" else "✘"
                    runAsync {
                        sleep(2000)
                    } ui {
                        feedback.text = ""
                    }
                }
            }
            region { hgrow = Priority.ALWAYS}
            feedback = label {
                style {
                    fontSize = 30.px
                }
            }
            region { hgrow = Priority.ALWAYS}
            button {
                text = "reset"
                action {
                    stage.children.forEach {
                        it as StageCard
                        it.label.text = ""
                        with (it.rect) {
                            width = 60.0
                            isVisible = true
                        }
                    }
                    cards.clear()
                    cards.addAll(
                        words
                            .map { WordCard(it, EfasColors.BLUE) }
                            .shuffled()
                    )

                }
            }
            style {
                padding = box (15.px, 10.px)
            }
        }
        addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
        addEventFilter(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
        addEventFilter(MouseEvent.MOUSE_RELEASED, ::stopDrag)
        addEventFilter(MouseEvent.MOUSE_EXITED, ::stopDrag)
    }

    fun startDrag (e: MouseEvent) {
        val poPt = pool.sceneToLocal(e.sceneX, e.sceneY)
        val stPt = stage.sceneToLocal(e.sceneX, e.sceneY)
        val paPt = root.sceneToLocal(e.sceneX, e.sceneY)
        if (pool.contains(poPt)) {
            cards
                .firstOrNull {
                    val mousePt = it.sceneToLocal(e.sceneX, e.sceneY)
                    it.contains(mousePt)
                }
                .apply {
                    this?.let {
                        flightLabel.text = this.label.text
                        flightBox.width = this.rect.width + 20.0
                        inFlight.layoutXProperty().value = paPt.x - inFlight.width
                        inFlight.layoutYProperty().value = paPt.y - inFlight.height
                        inFlight.isVisible = true
                        cards.remove(this)
                    }
                }
        } else if (stage.contains(stPt)) {
            staging
                .firstOrNull {
                    val mousePt = it.sceneToLocal(e.sceneX, e.sceneY)
                    it.contains(mousePt) && !it.label.text.isNullOrEmpty()
                }
                .apply {
                    this?.let {
                        flightLabel.text = it.label.text
                        flightBox.width = (it.label.text.length * 15.0) + 20.0
                        it.label.text = ""
                        it.rect.width = 60.0
                        it.rect.isVisible = true
                        inFlight.isVisible = true
                    }
                }
        }
    }

    fun animateDrag (e: MouseEvent) {
        val mousePt = root.sceneToLocal( e.sceneX, e.sceneY )
        if (root.contains(mousePt)) {
            inFlight.relocate(mousePt.x - inFlight.width, mousePt.y - inFlight.height)
        }
    }

    fun stopDrag (e: MouseEvent) {
        var happened = false
        if (inFlight.isVisible) {
            staging
                .firstOrNull {
                    val mousePt = it.sceneToLocal(e.sceneX - (flightBox.width/2), e.sceneY - (flightBox.height/2))
                    it.contains(mousePt)
                }.apply {
                    this?.let {
                        if (!label.text.isNullOrEmpty()) cards.add(WordCard(label.text, EfasColors.BLUE))
                        label.text = flightLabel.text
                        rect.width = 10.0
                        rect.isVisible = false
                        happened = true
                    }
                }
            inFlight.isVisible = false
            if (!happened) cards.add(WordCard(flightLabel.text, EfasColors.BLUE))
        }
    }


    class WordCard (content: String, color: EfasColors) : StackPane() {
        val label = Label()
        val rect = Rectangle()
        init {
            with (label) {
                text = content
                style {
                    fontSize = 20.px
                    textFill = c(color.shadow)
                    padding = box(8.px)
                }
            }
            with(rect) {
                width = content.length * 15.0
                height = 40.0
                arcHeight = 10.0
                arcWidth = 10.0
                fill = c(color.primary)
            }
            with(this) {
                add(rect)
                add(label)
            }
        }
    }

    class StageCard : StackPane() {
        val label = Label()
        val rect = Rectangle()
        init {
            with (label) {
                style {
                    fontSize = 20.px
                    textFill = c(EfasColors.MONO.shadow)
                    padding = box(8.px, 0.px)
                }
            }
            with(rect) {
                width = 60.0
                height = 30.0
                arcHeight = 10.0
                arcWidth = 10.0
                fill = c(EfasColors.GREY.primary)
            }
            with(this) {
                add(rect)
                add(label)
            }
        }
    }
}