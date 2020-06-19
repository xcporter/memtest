package ddexamples

import EfasColors
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.lang.Thread.sleep

class ColumnConnect : View() {
    var workarea: Pane by singleAssign()
    var indicator: Line by singleAssign()
    var leftFields = listOf<ConnectableField>()
    var rightFields = listOf<ConnectableField>()
    var view: BorderPane by singleAssign()
    var tg: ConnectableField? = null
    val source = SimpleObjectProperty<ConnectableField>()
    val answers = observableListOf<Boolean>()
    var feedback: Label by singleAssign()

    init {
        indicator = Line().apply {
            strokeWidth = 3.0
            stroke = c(EfasColors.GREY.shadow)
            isVisible = false
        }

        leftFields = EfasColors.values()
            .filter { it != EfasColors.GREY && it != EfasColors.MONO }
            .map { ConnectableField(it) }
        rightFields = EfasColors.values()
            .filter { it != EfasColors.GREY && it != EfasColors.MONO }
            .shuffled()
            .map { ConnectableField(it) }

        answers.onChange {
            if (answers.size == leftFields.size) {
                if (answers.firstOrNull { it == false } == null) feedback.text = "✔︎" else feedback.text = "✘"
                runAsync {
                    sleep(2000)
                } ui {
                    workarea.children
                        .filter { it != indicator }
                        .forEach { it.removeFromParent() }
                    feedback.text = ""
                    leftFields.map { it.occupied = false }
                    rightFields.map { it.occupied = false }
                    answers.clear()
                }
            }
        }
    }

    override val root = stackpane {
        maxHeight = 300.0
        view = borderpane {
            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
            left {
                vbox (10.0) {
                    leftFields.forEach {
                        add(it)
                    }
                    style {
                        padding = box(20.px)
                    }
                }
            }
            center = text ("connect all matching colors") {
                wrappingWidth = 100.0
                textAlignment = TextAlignment.CENTER
            }
            right {
                vbox (10.0) {
                    rightFields.forEach {
                        add(it)
                    }
                    style {
                        padding = box(20.px)
                    }
                }
            }
            bottom {
                hbox {
                    alignment = Pos.CENTER
                    feedback = label {
                        text = ""
                        style {
                            fontSize = 40.px
                        }
                    }
                }
            }
        }

        workarea = pane {
            add(indicator)
        }
        addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
        addEventFilter(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
        addEventFilter(MouseEvent.MOUSE_RELEASED, ::stopDrag)
        addEventFilter(MouseEvent.MOUSE_EXITED, ::stopDrag)
    }

    fun startDrag(e: MouseEvent) {
        val left = view.left.sceneToLocal(e.sceneX, e.sceneY)
        val right = view.right.sceneToLocal(e.sceneX, e.sceneY)
        if (view.left.contains(left)) {
            leftFields
                .firstOrNull {
                    val mousePt = it.sceneToLocal(e.sceneX, e.sceneY)
                    it.contains(mousePt) && !it.occupied
                }
                .apply {
                    this?.let {
                        with(indicator) {
                            startX = it.layoutX + (it.width/2)
                            startY = it.layoutY + (it.height/2)
                            endX = it.layoutX + (it.width/2)
                            endY = it.layoutY + (it.height/2)
                            isVisible = true
                            source.value = it
                        }
                    }
                }
        }

        if (view.right.contains(right)) {
            rightFields
                .firstOrNull {
                    val mousePt = it.sceneToLocal(e.sceneX, e.sceneY)
                    it.contains(mousePt) && !it.occupied
                }
                .apply {
                    this?.let {
                        with(indicator) {
                            startX = it.layoutX + (it.width/2) + (scene.width - 90.0)
                            startY = it.layoutY + (it.height/2)
                            endX = it.layoutX + (it.width/2) + (scene.width - 90.0)
                            endY = it.layoutY + (it.height/2)
                            isVisible = true
                            source.value = it
                        }
                    }
                }
        }
    }

    fun animateDrag(e: MouseEvent) {
        val spacePt = workarea.sceneToLocal(e.sceneX, e.sceneY)
        if (workarea.contains(spacePt) && indicator.isVisible) {
            with(indicator) {
                endX = spacePt.x
                endY = spacePt.y
            }
        }
    }

    fun stopDrag(e: MouseEvent) {
        val left = view.left.sceneToLocal(e.sceneX, e.sceneY)
        val right = view.right.sceneToLocal(e.sceneX, e.sceneY)
        if (indicator.isVisible) {
            if (view.left.contains(left)) {
                leftFields
                    .firstOrNull {
                        val mousePt = it.sceneToLocal(e.sceneX, e.sceneY)
                        it.contains(mousePt) && !it.occupied
                    }
                    .apply {
                        this?.let {
                            if (rightFields.contains(source.value)) {
                                val tmp = Line()
                                with(tmp) {
                                    startX = indicator.startX
                                    startY = indicator.startY
                                    endY = it.layoutY + it.height / 2
                                    endX = it.layoutX + it.width / 2
                                    strokeWidth = 3.0
                                    stroke = c(EfasColors.GREEN.shadow)
                                }
                                workarea.add(tmp)
                                it.occupied = true
                                rightFields
                                    .firstOrNull { it == source.value }
                                    ?.occupied = true

                                answers.add((it.fill == source.value.fill))
                            }
                        }
                    }
            }
            if (view.right.contains(right)) {
                rightFields
                    .firstOrNull {
                        val mousePt = it.sceneToLocal(e.sceneX, e.sceneY)
                        it.contains(mousePt) && !it.occupied
                    }
                    .apply {
                        this?.let {
                            if (leftFields.contains(source.value)) {
                                val tmp = Line()
                                with(tmp) {
                                    startX = indicator.startX
                                    startY = indicator.startY
                                    endY = it.layoutY + it.height/2
                                    endX = it.layoutX + it.width/2 + 235.0
                                    strokeWidth = 3.0
                                    stroke = c(EfasColors.GREEN.shadow)
                                }
                                workarea.add(tmp)
                                it.occupied = true
                                leftFields
                                    .firstOrNull { it == source.value }
                                    ?.occupied = true

                                answers.add((it.fill == source.value.fill))
                            }
                        }
                    }
            }
        }
        indicator.isVisible = false
    }

    class ConnectableField (color: EfasColors) : Rectangle() {
        var occupied: Boolean = false
        init {
            with(this) {
                width = 50.0
                height = 50.0
                arcHeight = 10.0
                arcWidth = 10.0
                fill = c(color.primary)
            }
        }

    }
}