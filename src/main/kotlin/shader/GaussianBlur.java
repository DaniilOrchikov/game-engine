package shader;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GaussianBlur {
    private int width , height;
    private final int fbo; // идентификатор фреймбуфера
    private final Texture texture; // текстура, на которую будем рендерить результат
    private final Image im;

    public GaussianBlur(int width, int height, Image im) throws SlickException {
        this.width = width;
        this.height = height;
        this.im = im;

        // Инициализация текстуры и фреймбуфера
        texture = im.getTexture(); // метод createTexture - просто создает текстуру с заданными параметрами и возвращает объект Texture
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTextureID(), 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void render(ShaderProgram blurShader) {
//        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
//        glViewport(0, 0, width, height);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        glLoadIdentity();
//        blurShader.use();
//        blurShader.setUniformi("image", 0);
//        inputTexture.bind();
//        glBegin(GL_QUADS);
//        glVertex3f(-1, 1, 0);
//        glVertex3f(1, 1, 0);
//        glVertex3f(1, -1, 0);
//        glVertex3f(-1, -1, 0);
//        glEnd();
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//        blurShader.stop();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
//        glViewport(0, 0, width, height);
        blurShader.use();

        im.getTexture().bind();
        glLoadIdentity();
        glTranslatef(0, 0, -10f);
        glColor3f(1, 1, 1);
        glBegin(GL_QUADS);
        glVertex3f(-1, 1, 0);
        glVertex3f(1, 1, 0);
        glVertex3f(1, -1, 0);
        glVertex3f(-1, -1, 0);
        glEnd();

        blurShader.stop();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Image getImage() {
        return im;
    }

    public void dispose() {
        glDeleteFramebuffers(fbo);
        texture.release();
    }

}
