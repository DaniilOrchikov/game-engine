import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

public class ImageBlur {
    private int shaderProgramID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private Texture texture;

    public void init() {
        // Load texture
        try {
            texture = TextureLoader.getTexture("PNG", new File("src/main/resources/g.png").toURI().toURL().openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Compile and link shaders
        shaderProgramID = GL20.glCreateProgram();
        vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        GL20.glShaderSource(vertexShaderID, """
                #version 330 core
                layout (location = 0) in vec3 aPos;
                layout (location = 1) in vec2 aTexCoord;
                out vec2 TexCoord;
                void main()
                {
                   gl_Position = vec4(aPos, 1.0);
                   TexCoord = aTexCoord;
                }
                """);
        GL20.glCompileShader(vertexShaderID);

        GL20.glShaderSource(fragmentShaderID, """
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
                """);
        GL20.glCompileShader(fragmentShaderID);

        GL20.glAttachShader(shaderProgramID, vertexShaderID);
        GL20.glAttachShader(shaderProgramID, fragmentShaderID);
        GL20.glLinkProgram(shaderProgramID);
        GL20.glValidateProgram(shaderProgramID);
    }

    public void render() {
        GL20.glUseProgram(shaderProgramID);

        // Bind texture
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        texture.bind();

        // Set up vertex and texture coordinate data
        float[] vertices = {
                -1f, 1f,
                1f, 1f,
                -1f, -1f,
                1f, -1f
        };
        float[] texCoords = {
                0f, 0f,
                1f, 0f,
                0f, 1f,
                1f, 1f
        };
        FloatBuffer bv = BufferUtils.createFloatBuffer(8);
        bv.put(vertices);
        FloatBuffer bt = BufferUtils.createFloatBuffer(8);
        bt.put(texCoords);

        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 2, false, 0, bv);

        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 2, false, 0, bt);

        // Draw quad
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        // Unbind texture

        GL20.glUseProgram(0);
    }

    public void cleanup() {
        GL20.glDetachShader(shaderProgramID, vertexShaderID);
        GL20.glDetachShader(shaderProgramID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(shaderProgramID);
    }
}
