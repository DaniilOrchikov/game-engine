package shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Circle;

public class TestGame extends BasicGame {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private ShaderProgram blurShader;
    private Image backgroundTexture;

    public TestGame() {
        super("Test Game");
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        blurShader = ShaderProgram.create(() -> """
                        #version 330 core
                         
                         layout (location = 0) in vec3 aPos; // position attribute
                         layout (location = 1) in vec2 aTexCoord; // texture coordinate attribute
                         
                         out vec2 TexCoord;
                         
                         void main()
                         {
                             gl_Position = vec4(aPos, 1.0); // set the position of the vertex
                             TexCoord = aTexCoord; // pass the texture coordinate to the fragment shader
                         }""",
                () -> """
                        #version 330 core
                         
                         in vec2 TexCoord; // texture coordinates passed from the vertex shader
                         
                         uniform sampler2D image; // texture to apply the blur to
                         
                         out vec4 OutColor;
                         
                         void main()
                         {
                             vec2 tex_offset = 1.0 / textureSize(image, 0); // size of one texture pixel
                             float kernel[9] = float[](
                                 0.1, 0.2, 0.1,
                                 0.2, 0.4, 0.2,
                                 0.1, 0.2, 0.1
                             ); // Gaussian filter kernel
                             float kernel_sum = 16.0; // sum of kernel values
                         
                             vec4 color_sum = vec4(0.0); // accumulator variable for color sum
                             for(int i=-1; i<=1; i++)
                             {
                                 for(int j=-1; j<=1; j++)
                                 {
                                     vec4 color = texture(image, TexCoord + vec2(i, j) * tex_offset); // texel color with an offset
                                     color_sum += color * kernel[(i+1)*3 + (j+1)]; // sum the colors considering the kernel weights
                                 }
                             }
                             OutColor = vec4(color_sum.rgb / kernel_sum, 1.0); // normalize the result and write it to the output color
                         }""");

        backgroundTexture = new Image(WIDTH, HEIGHT);
        backgroundTexture.getGraphics().setColor(new Color(255, 0, 0, 255));
        backgroundTexture.getGraphics().fill(new Circle(WIDTH / 2f, HEIGHT / 2f, WIDTH / 2f));
    }

    @Override
    public void update(GameContainer container, int delta) {
        // ...
    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {

        backgroundTexture.getGraphics().setColor(new Color(255, 0, 0, 255));
        backgroundTexture.getGraphics().fill(new Circle(WIDTH / 2f, HEIGHT / 2f, WIDTH / 4f));
        g.flush();
//        g.flush();
//        blurShader.use();
//        int imageUniform = blurShader.findUniform("image");
//        GL20.glUniform1i(imageUniform, 0);
//
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, backgroundTexture.getTexture().getTextureID());
//
//        g.drawImage(backgroundTexture, 0, 0);
//        blurShader.stop();
        Image im = backgroundTexture.copy();
        g.drawImage(im, 0, 0);
        blurShader.use();

        int imageUniform = blurShader.findUniform("image");
        GL20.glUniform1i(imageUniform, 0);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, im.getTexture().getTextureID());

        g.drawImage(im, 0, 0);

        blurShader.stop();
//        g.clear();
//        g.drawImage(backgroundTexture, 0, 0);
//        g.flush();
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new TestGame());
        app.setDisplayMode(WIDTH, HEIGHT, false);
        app.start();
    }
}