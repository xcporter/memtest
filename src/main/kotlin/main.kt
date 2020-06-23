import com.jpro.webapi.JProApplication
import com.jpro.webapi.WebAPI
import javafx.stage.Stage
import tornadofx.*

class TestApp : App (MainView::class, Style::class)

fun main () {
    launch<TestApp>()
}


class WebScope(val stage: Stage) : Scope() {
    val webAPI: WebAPI get() = WebAPI.getWebAPI(stage)
}

class JProMain : JProApplication () {
    private val app = TestApp()

    override fun start(primaryStage: Stage) {
        app.scope = WebScope(primaryStage)
        app.start(primaryStage)
    }

    override fun stop() {
        app.stop()
        super.stop()
    }
}

