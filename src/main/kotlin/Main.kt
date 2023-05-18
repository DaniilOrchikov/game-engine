import org.newdawn.slick.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.random.Random

class MyGame(title: String) : BasicGame(title) {

    private val polygons = ArrayList<MyPolygon>()
    private val light = Light(200f, 200f, 250f)
    private val light1 = Light(700f, 200f, 3000f)
    private lateinit var surface: Image
    private lateinit var g: Graphics
    private val pool = Executors.newFixedThreadPool(7)

    override fun init(gc: GameContainer) {
        polygons.add(
            MyPolygon(
                floatArrayOf(
                    20f, 20f,
                    WIDTH.toFloat() / 2 - 20, 20f,
                    WIDTH.toFloat() / 2 - 20, HEIGHT.toFloat() / 2 - 20,
                    20f, HEIGHT.toFloat() / 2 - 20
                )
            )
        )
        surface = Image(WIDTH / 2, HEIGHT / 2)
        surface.filter = Image.FILTER_NEAREST
        g = surface.graphics
    }

    //Кнопка нажата
    override fun keyPressed(key: Int, c: Char) {

    }

    //Кнопка отжата
    override fun keyReleased(key: Int, c: Char) {
    }

    //Мышь нажата
    override fun mousePressed(buttom: Int, x: Int, y: Int) {
        polygons.add(
            MyPolygon(
                floatArrayOf(
                    Random.nextDouble((x / 2 - 100).toDouble(), (x / 2 - 20).toDouble()).toFloat(),
                    Random.nextDouble((y / 2 - 100).toDouble(), (y / 2 - 20).toDouble()).toFloat(),
                    Random.nextDouble((x / 2 + 20).toDouble(), (x / 2 + 100).toDouble()).toFloat(),
                    Random.nextDouble((y / 2 - 100).toDouble(), (y / 2 - 20).toDouble()).toFloat(),
                    Random.nextDouble((x / 2 + 20).toDouble(), (x / 2 + 100).toDouble()).toFloat(),
                    Random.nextDouble((y / 2 + 20).toDouble(), (y / 2 + 100).toDouble()).toFloat(),
                )
            )
        )
    }

    //Мышь отжата
    override fun mouseClicked(button: Int, x: Int, y: Int, clickCount: Int) {
    }

    //Мышь зажата и двигается
    override fun mouseDragged(oldx: Int, oldy: Int, newx: Int, newy: Int) {
    }

    //Мышь двигается
    override fun mouseMoved(oldx: Int, oldy: Int, newx: Int, newy: Int) {
        light.x = newx.toFloat() / 2
        light.y = newy.toFloat() / 2
    }

    //Мышь отжата (лучше не использовать)
    override fun mouseReleased(button: Int, x: Int, y: Int) {
    }

    //Колесико мыши крутится
    override fun mouseWheelMoved(change: Int) {
    }

    override fun update(gc: GameContainer, delta: Int) {
        val f1 = pool.submit {
            light.update(polygons)
        }
        val f2 = pool.submit {
            light1.update(polygons)
        }

        try {
            f1.get()
            f2.get()
        } catch (_: InterruptedException) {
        } catch (_: ExecutionException) {
        }
    }

    override fun render(gc: GameContainer, graphics: Graphics) {
        g.color = Color(0, 0, 0)
        g.fillRect(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat())
        g.color = Color(255, 255, 255)
        for (obj in polygons) obj.render(g)
        light.render(g)
        light1.render(g)
        graphics.drawImage(surface.getScaledCopy(2f), 0f, 0f)
        graphics.flush()
        g.flush()
    }
}

fun main() {
    val gameContainer = AppGameContainer(MyGame("test"))
    gameContainer.setDisplayMode(WIDTH, HEIGHT, FULLSCREEN)
//    gameContainer.setVSync(true)
    gameContainer.start()
}