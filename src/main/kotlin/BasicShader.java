import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import shader.ShaderProgram;

/**
 *
 */
public class BasicShader {
    public static void main(String[] args) throws Exception {
        init();

        ShaderProgram sp = ShaderProgram.create(
                ()-> """
                        varying vec4 vertColor;
                                                
                        void main() {
                            gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
                            vertColor = vec4(0.6, 0.3, 0.4, 1.0);
                        }""",
                ()-> """
                        varying vec4 vertColor;
                                                
                        void main() {
                            gl_FragColor = vertColor;
                        }""");
        ShaderProgram sp1 = ShaderProgram.create(
                ()-> """
                         #version 330 core
                         
                         layout (location = 0) in vec3 aPos; // position attribute
                         layout (location = 1) in vec2 aTexCoord; // texture coordinate attribute
                         
                         out vec2 TexCoord;
                         
                         void main()
                         {
                             gl_Position = vec4(aPos, 1.0); // set the position of the vertex
                             TexCoord = aTexCoord; // pass the texture coordinate to the fragment shader
                         }""",
                ()-> """
                         #version 330 core
                         
                         in vec2 TexCoord; // texture coordinates passed from the vertex shader
                         
                         uniform sampler2D image; // texture to apply the blur to
                         
                         out vec4 OutColor;
                         
                         void main()
                         {
                             vec2 tex_offset = 1.0 / textureSize(image, 0); // size of one texture pixel
                             float kernel[9] = float[](
                                 1, 2, 1,
                                 2, 4, 2,
                                 1, 2, 1
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
        while (!Display.isCloseRequested()) {

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();

            // --------------------------
            sp.use();

            glLoadIdentity();
            glTranslatef(0, 0, -10f);
            glColor3f(1, 1, 1);
            glBegin(GL_QUADS);
            glVertex3f(-1, 1, 0);
            glVertex3f(1, 1, 0);
            glVertex3f(1, -1, 0);
            glVertex3f(-1, -1, 0);
            glEnd();

            sp.stop();


            sp1.use();

            glLoadIdentity();
            glTranslatef(0, 0, -10f);
            glColor3f(1, 1, 1);
            glBegin(GL_TRIANGLES);
            glVertex2f(1, 1);
            glVertex2f(-1, 1);
            glVertex2f(-1, -1);
            glEnd();

            sp1.stop();


            // --------------------------

            Display.update();
            Display.sync(30);
        }

        Display.destroy();
    }

    static void init() {
        int w = 1024;
        int h = 768;
        try {
            Display.setDisplayMode(new DisplayMode(w, h));
            Display.setTitle("Basic shader example");
            Display.create();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        glViewport(0, 0, w, h);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(45, (1f * w / h), 0.1f, 100f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glShadeModel(GL_SMOOTH);
        glClearColor(0, 0, 0, 0);
        glClearDepth(1);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);


    }
}