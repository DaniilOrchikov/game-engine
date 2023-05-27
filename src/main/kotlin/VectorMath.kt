import org.lwjgl.util.vector.Vector4f
import org.newdawn.slick.geom.Vector2f
import kotlin.math.*

fun findIntersection(x:Float, y:Float, segment1: Vector2f, segment2: Vector4f): Vector2f? {
    val x1: Float = x
    val y1: Float = y
    val x2: Float = segment1.x + x
    val y2: Float = segment1.y + y
    val x3: Float = segment2.x
    val y3: Float = segment2.y
    val x4: Float = segment2.z
    val y4: Float = segment2.w
    val d = ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1))
    if (d == 0f) return null
    val ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / d
    val ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / d
    return if (ua < 0 || ua > 1 || ub < 0 || ub > 1) null else Vector2f((x1 + ua * (x2 - x1)), (y1 + ua * (y2 - y1)))

}

fun sgn(x: Float): Int {
    return if (x > 0) 1 else if (x == 0f) 0 else -1
}

fun cross(v1: Vector2f, v2: Vector2f): Float {
    return v1.x * v2.y - v1.y * v2.x
}

fun distance(point1: Vector2f, point2: Vector2f): Float {
    return (point1.x - point2.x).pow(2) + (point1.y - point2.y).pow(2)
}

fun distance(x: Float, y: Float, point2: Vector2f): Float {
    return (x - point2.x).pow(2) + (y - point2.y).pow(2)
}

fun collinearityCheck(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
    if (x2 == 0f || y2 == 0f) return false
    return round(x1 / x2, 3) == round(y1 / y2, 3)
}

fun round(x: Float, n: Int): Float {
    return (round(x * 10.0.pow(n)) / 10.0.pow(n)).toFloat()
}

fun angle(x: Float, y: Float): Float {
    val signX = sgn(x)
    val signY = sgn(y)

    val a = PI / 2 * (2 - (1f + signX) * (1 - sgn(y.pow(2))))
    val b = PI / 4 * (2 + signX) * signY
    val c = sgn(x * y) * atan((abs(x) - abs(y)) / (abs(x) + abs(y)))

    return (a - b - c).toFloat()
}