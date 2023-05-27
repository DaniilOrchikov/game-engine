import org.newdawn.slick.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.system.exitProcess

class MyGame(title: String) : BasicGame(title) {

    private val polygons = ArrayList<MyPolygon>()
    private val light = Light(100f, 100f, 200f)
    private val light1 = Light(100f, 100f, 2000f)
    private lateinit var surface: Image
    private lateinit var g: Graphics
    private val pool = Executors.newFixedThreadPool(7)

    override fun init(gc: GameContainer) {
        polygons.add(
            MyPolygon(
                floatArrayOf(
                    0f,0f,
                    WIDTH.toFloat() / SCALE, 0f,
                    WIDTH.toFloat() / SCALE + 1, HEIGHT.toFloat() / SCALE + 1,
                    0f, HEIGHT.toFloat() / SCALE + 1
                )
            )
        )
        surface = Image(WIDTH / SCALE, HEIGHT / SCALE)
        surface.filter = Image.FILTER_NEAREST
        g = surface.graphics
        light.init()
        light1.init()
    }

    //Кнопка нажата
    override fun keyPressed(key: Int, c: Char) {
        if (key == Input.KEY_ESCAPE) {
            light.exit()
//            light1.exit()
            pool.shutdown()
            try {
                pool.awaitTermination(5, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            exitProcess(0);
        }
    }

    //Кнопка отжата
    override fun keyReleased(key: Int, c: Char) {
    }

    //Мышь нажата
    override fun mousePressed(buttom: Int, x: Int, y: Int) {
        polygons.add(
            MyPolygon(
                floatArrayOf(
                    Random.nextDouble((x / SCALE - 50).toDouble(), (x / SCALE - 10).toDouble()).toFloat(),
                    Random.nextDouble((y / SCALE - 50).toDouble(), (y / SCALE - 10).toDouble()).toFloat(),
                    Random.nextDouble((x / SCALE + 10).toDouble(), (x / SCALE + 50).toDouble()).toFloat(),
                    Random.nextDouble((y / SCALE - 50).toDouble(), (y / SCALE - 10).toDouble()).toFloat(),
                    Random.nextDouble((x / SCALE + 10).toDouble(), (x / SCALE + 50).toDouble()).toFloat(),
                    Random.nextDouble((y / SCALE + 10).toDouble(), (y / SCALE + 50).toDouble()).toFloat(),
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
        light.x = newx.toFloat() / SCALE
        light.y = newy.toFloat() / SCALE
    }

    //Мышь отжата (лучше не использовать)
    override fun mouseReleased(button: Int, x: Int, y: Int) {
    }

    //Колесико мыши крутится
    override fun mouseWheelMoved(change: Int) {
    }

    override fun update(gc: GameContainer, delta: Int) {
        light.update(polygons)
        light1.update(polygons)
    }

    override fun render(gc: GameContainer, graphics: Graphics) {
        g.clear()
        light.render(g)
        light1.render(g)
        g.color = Color(255, 255, 255)
        for (obj in polygons) obj.render(g)
        graphics.drawImage(surface.getScaledCopy(SCALE.toFloat()), 0f, 0f)
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
