import tornadofx.*

class Style : Stylesheet() {
    companion object {
        val header by cssclass()
        val background by cssclass()
    }

    init {
        root {
            prefWidth = 325.px
            prefHeight = 575.px
        }
        header {
            backgroundColor += c(EfasColors.MONO.shadow)
            padding = box(12.px, 20.px)
        }
        background {
            backgroundColor += c(EfasColors.MONO.highlight)
            padding = box(20.px)
        }
    }

}