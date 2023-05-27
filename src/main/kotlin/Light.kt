import org.lwjgl.opengl.GL11
import org.newdawn.slick.Color
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import org.newdawn.slick.geom.Circle
import org.newdawn.slick.geom.Polygon
import org.newdawn.slick.geom.Vector2f
import java.util.concurrent.ExecutionException
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class Light(var x: Float, var y: Float, private val radius: Float) {
    private val rays = ArrayList<Vector2fWithAngle>()
    private val forkJoinPool = ForkJoinPool()
    private val piPerMilli = PI.toFloat() / 10000f
    private lateinit var surface: Image
    private lateinit var g: Graphics
    private val k = 5000f

    private lateinit var shadowIm: Image
    fun init() {
        shadowIm = Image(WIDTH / SCALE * 2, HEIGHT / SCALE * 2)
        shadowIm.graphics.fill(Circle(shadowIm.width.toFloat() / 2, shadowIm.height.toFloat() / 2, radius))
        surface = Image(WIDTH / SCALE, HEIGHT / SCALE)
        g = surface.graphics
    }

    fun update(polygons: ArrayList<MyPolygon>) {
        val newRays = ArrayList<Vector2fWithAngle>()
        for (i in polygons) {
            for (j in i.points) {
                if (distance(x, y, j) <= k.pow(2)) {
                    val angle = angle(j.x - x, j.y - y)
                    var cs = cos(angle + piPerMilli)
                    var si = sin(angle + piPerMilli)
                    newRays.add(
                        Vector2fWithAngle(
                            Vector2f(cs * k, si * k),
                            angle + piPerMilli
                        )
                    )
                    cs = cos(angle - piPerMilli)
                    si = sin(angle - piPerMilli)
                    newRays.add(
                        Vector2fWithAngle(
                            Vector2f(cs * k, si * k),
                            angle - piPerMilli
                        )
                    )
                }
            }
        }
        val future = forkJoinPool.submit(RayTask(x, y, polygons, newRays, 0, newRays.size))
        try {
            future.get()
        } catch (_: InterruptedException) {
        } catch (_: ExecutionException) {
        }

        rays.clear()
        rays.addAll(newRays)
        rays.sortWith { a, b -> compareValues(a.angle, b.angle) }
    }

    fun render(graphics: Graphics) {
        g.clear()
        g.color = Color(255, 255, 0, 250)
        for ((i, ray) in rays.withIndex()) {
            g.fill(
                Polygon(
                    floatArrayOf(
                        x, y,
                        ray.vec.x + x, ray.vec.y + y,
                        rays[(i + 1) % rays.size].vec.x + x, rays[(i + 1) % rays.size].vec.y + y
                    )
                )
            )
        }
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA)
        g.drawImage(shadowIm, x - shadowIm.width / 2, y - shadowIm.height / 2)
        GL11.glDisable(GL11.GL_BLEND)
        g.setDrawMode(Graphics.MODE_NORMAL)
        graphics.drawImage(surface, 0f, 0f)
    }

    inner class RayTask(
        private val x: Float, private val y: Float,
        private val polygons: ArrayList<MyPolygon>, private val rays: MutableList<Vector2fWithAngle>,
        private val start: Int, private val end: Int
    ) : RecursiveAction() {

        override fun compute() {
            if (end - start <= 4) {
                for (i in start until end) {
                    findingIntersections(rays[i], polygons)
                }
            } else {
                val mid = start + (end - start) / 2
                invokeAll(
                    RayTask(x, y, polygons, rays, start, mid),
                    RayTask(x, y, polygons, rays, mid, end)
                )
            }
        }
    }

    fun findingIntersections(ray: Vector2fWithAngle, polygons: ArrayList<MyPolygon>) {
        var point = Vector2f(ray.vec.x, ray.vec.y)
        for (obj in polygons) {
            for (line in obj.lines) {
                val p = findIntersection(x, y, ray.vec, line) ?: continue
                point = if (distance(x, y, Vector2f(point)) < distance(x, y, Vector2f(p))) point else p
            }
        }
        ray.vec.x = point.x - x
        ray.vec.y = point.y - y
    }

    fun exit() {
        forkJoinPool.shutdown()
        try {
            forkJoinPool.awaitTermination(5, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}