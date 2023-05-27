import org.lwjgl.util.vector.Vector4f
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Polygon
import org.newdawn.slick.geom.Vector2f

class MyPolygon(points: FloatArray) {
    private val polygon: Polygon
    val points = ArrayList<Vector2f>()
    val lines = ArrayList<Vector4f>()

    init {
        polygon = Polygon(points)
        for (i in points.indices step 2)
            this.points.add(Vector2f(points[i], points[i + 1]))
        for (i in points.indices step 2)
            lines.add(
                Vector4f(
                    points[i],
                    points[i + 1],
                    points[(i + 2) % points.size],
                    points[(i + 3) % points.size]
                )
            )
    }

    fun render(graphics: Graphics) {
        graphics.draw(polygon)
    }
}