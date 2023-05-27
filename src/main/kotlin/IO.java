import com.google.common.io.ByteStreams;
import shader.ShaderProgram;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * IO utilities.
 */
public final class IO {
    /**
     * Creates a supplier from the given resource.
     * @param resourceName
     * @return
     */
    public static Supplier<String> fromResource(String resourceName) {
        return () -> {
            try (InputStream in = ShaderProgram.class.getResourceAsStream(resourceName)) {
                assert in != null;
                return new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}