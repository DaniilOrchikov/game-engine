import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import org.newdawn.slick.Color
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Polygon
import org.newdawn.slick.geom.ShapeRenderer
import java.util.concurrent.ExecutionException
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction
import kotlin.math.*


class Light(var x: Float, var y: Float, private val r: Float) {
    private val rays = ArrayList<Vector4fWithAngle>()
    private val forkJoinPool = ForkJoinPool()
    private val rayCount = 300
    private val angles = Array(rayCount) { Vector3f(0f, 0f, 0f) }
    private val piPerMilli = PI.toFloat() / 1000f

    init {
        repeat(rayCount) { i ->
            val angle = (2 * PI / rayCount * i).toFloat()
            angles[i].x = cos(angle)
            angles[i].y = sin(angle)
            angles[i].z = angle
        }
    }

    fun update(polygons: ArrayList<MyPolygon>) {
        val newRays = ArrayList<Vector4fWithAngle>()
        repeat(rayCount) { i ->
            val cs = angles[i].x
            val si = angles[i].y
            newRays.add(
                Vector4fWithAngle(
                    Vector4f(
                        x, y,
                        cs * r + x, si * r + y
                    ), angles[i].z, "ordinary"
                )
            )
        }
        for (i in polygons) {
            for (j in i.points) {
                if (distance(x, y, j) <= r.pow(2)) {
                    val angle = angle(j.x - x, j.y - y)
                    newRays.add(
                        Vector4fWithAngle(
                            Vector4f(x, y, j.x, j.y),
                            angle, "central"
                        )
                    )
                    var cs = cos(angle + piPerMilli)
                    var si = sin(angle + piPerMilli)
                    newRays.add(
                        Vector4fWithAngle(
                            Vector4f(x, y, cs * r + x, si * r + y),
                            angle + piPerMilli, "side"
                        )
                    )
                    cs = cos(angle - piPerMilli)
                    si = sin(angle - piPerMilli)
                    newRays.add(
                        Vector4fWithAngle(
                            Vector4f(x, y, cs * r + x, si * r + y),
                            angle - piPerMilli, "side"
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
        newRays.sortWith { a, b -> compareValues(a.angle, b.angle) }
        rays.clear()
        val arr = ArrayList<Vector4fWithAngle>()
        for (i in newRays) {
            if (i.type == "central") rays.add(i)
            else if (i.type == "side") {
                rays.add(i)
                if (distance(x, y, Vector2f(i.vec.z, i.vec.w)) < r.pow(2)) {
                    arr.clear()
                } else {
                    rays.addAll(arr)
                    arr.clear()
                }
            } else
                arr.add(i)
        }
        rays.addAll(arr)
        rays.sortWith { a, b -> compareValues(a.angle, b.angle) }
//        rays.add(newRays[0])
//        for (i in 1 until newRays.size) {
//            if (!newRays[i].deletable ||
//                !(newRays[i - 1].vec.z == newRays[i].vec.z && newRays[i].vec.z == newRays[(i + 1) % newRays.size].vec.z ||
//                        newRays[i - 1].vec.w == newRays[i].vec.w && newRays[i].vec.w == newRays[(i + 1) % newRays.size].vec.w ||
//                        collinearityCheck(
//                            newRays[i].vec.z - newRays[i - 1].vec.z,
//                            newRays[i].vec.w - newRays[i - 1].vec.w,
//                            newRays[(i + 1) % newRays.size].vec.z - newRays[i].vec.z,
//                            newRays[(i + 1) % newRays.size].vec.w - newRays[i].vec.w
//                        ))
//            ) rays.add(newRays[i])
//        }
    }

    fun render(graphics: Graphics) {
        graphics.color = Color(255, 255, 255, 100)
        for ((i, ray) in rays.withIndex()) {
            ShapeRenderer.fill(
                Polygon(
                    floatArrayOf(
                        x, y,
                        ray.vec.z, ray.vec.w,
                        rays[(i + 1) % rays.size].vec.z, rays[(i + 1) % rays.size].vec.w
                    )
                )
            )
        }
    }

    inner class RayTask(
        private val x: Float, private val y: Float,
        private val polygons: ArrayList<MyPolygon>, private val rays: MutableList<Vector4fWithAngle>,
        private val start: Int, private val end: Int
    ) : RecursiveAction() {

        override fun compute() {
            if (end - start <= 40) {
                for (i in start until end) {
                    val vec = rays[i]
                    var point = Vector2f(vec.vec.z, vec.vec.w)
                    for (obj in polygons) {
                        for (line in obj.lines) {
                            val p = findIntersection(vec.vec, line) ?: continue
                            point = if (distance(x, y, Vector2f(point)) < distance(x, y, Vector2f(p))) point else p
                        }
                    }
                    vec.vec.z = point.x
                    vec.vec.w = point.y
                    rays[i] = vec
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
}