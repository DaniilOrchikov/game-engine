import org.lwjgl.opengl.GL11.*
import org.newdawn.slick.*
import shader.ShaderProgram


class MMain(title: String) : BasicGame(title) {
    lateinit var texture: Image
    lateinit var texture1: Image
    lateinit var shader: ShaderProgram
    override fun init(gc: GameContainer) {
        texture = Image("g.png").getScaledCopy(6, 6)
        texture1 = Image("g.png").getScaledCopy(WIDTH / 2, HEIGHT / 2)
        shader = ShaderProgram.create(
            {
                """
            #version 330 core
                layout (location = 0) in vec3 aPos;
                layout (location = 1) in vec2 aTexCoord;
                out vec2 TexCoord;
                void main()
                {
                   gl_Position = vec4(aPos, 1.0);
                   TexCoord = aTexCoord;
                }
        """.trimIndent()
            }, {
                """
         #version 330 core
                in vec2 TexCoord;
                uniform sampler2D image;
                out vec4 OutColor;
                void main()
                {
                   vec2 tex_offset = 1.0 / textureSize(image, 0);
                   float kernel[9] = float[](
                       0.1, 0.2, 0.1,
                       0.2, 0.4, 0.2,
                       0.1, 0.2, 0.1
                   );
                   float kernel_sum = 16.0;
                   vec4 color_sum = vec4(0.0);
                   for(int i=-1; i<=1; i++)
                   {
                       for(int j=-1; j<=1; j++)
                       {
                           vec4 color = texture(image, TexCoord + vec2(i, j) * tex_offset);
                           color_sum += color * kernel[(i+1)*3 + (j+1)];
                       }
                   }
                   OutColor = vec4(color_sum.rgb / kernel_sum, 1.0);
                }
    """.trimIndent()
            })

    }

    override fun update(gc: GameContainer, delta: Int) {
    }

    override fun render(gc: GameContainer, graphics: Graphics) {
////        texture.bind()
//        GL11.glEnable(SGL.GL_POINT_SMOOTH)
////        GL11.glEnable(GL11.GL_BLEND)
////
////        GL11.glBlendFunc(SGL.GL_SRC_ALPHA, SGL.GL_ONE)
//        GL11.glPointSize(6f)
////        GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA)
//        graphics.drawImage(texture, 0f, 0f)
////        GL11.glEnable(GL11.GL_BLEND)
//        GL11.glEnable(SGL.GL_POINT_SMOOTH)
////        texture.bind()
////        shader.use()
////        shader.setUniformi("image", 0)
////        graphics.drawImage(texture, 0f, 0f)
////        shader.stop()
        graphics.drawImage(texture1, 0f, 0f)
    }
}

fun main() {
    val gameContainer = AppGameContainer(MMain("test"))
    gameContainer.setDisplayMode(WIDTH / SCALE, HEIGHT / SCALE, false)
    gameContainer.start()
}
