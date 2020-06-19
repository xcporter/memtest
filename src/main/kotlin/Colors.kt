import javafx.scene.paint.Color
import tornadofx.c

/**
 * Efas Graphic Standards color properties
 *
 * @property primary
 * @property shadow
 * @property highlight
 * @property midtone
 */

enum class EfasColors(val primary: String, val shadow: String, val highlight: String, val midtone: String) {
    GREEN ("#59C097", "#014436", "#D8EEE5", "#98D2B3"),
    RED ("#F47B5C","#65071E", "#FEF4F5", "#F4A19A"),
    YELLOW ("#FFDD75", "#68361A", "#FFFEF5", "#FFCB66"),
    BLUE ("#83C2D3", "#0B465F", "#F1F9FB", "#B4D7E5"),
    PINK ("#CB92C0", "#48325A", "#EAEAF5", "#B18EAC"),
    GREY ("#D9D7CF", "#525150", "#F0EFF0", "#A5A7AA"),
    MONO ("", "#414142", "#F7F7F8", "")
}