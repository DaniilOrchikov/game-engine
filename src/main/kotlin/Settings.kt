const val WIDTH = 1920
const val HEIGHT = 1080
const val SCALE = 2
const val FULLSCREEN = true
const val FPS = 60

fun pprint(vararg objs: Any, sep: String = ", ", end: String = "\n") {
    print("${Thread.currentThread().stackTrace[3]} ----- ${objs.joinToString(separator = sep)}$end")
}

