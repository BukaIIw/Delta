package hydrogen.render.gl;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * Small renderer-owned shader abstraction. No Minecraft shader registry,
 * RenderSystem, or vanilla BufferBuilder participates in this pipeline.
 */
public final class ShaderProgram implements AutoCloseable {
    private final int id;
    private final Map<String, Integer> uniforms = new HashMap<>();

    public static ShaderProgram fromResources(String vertexPath, String fragmentPath) {
        return new ShaderProgram(readResource(vertexPath), readResource(fragmentPath));
    }

    public ShaderProgram(String vertexSource, String fragmentSource) {
        int vertex = 0;
        int fragment = 0;
        int program = 0;
        try {
            vertex = compile(GL_VERTEX_SHADER, vertexSource);
            fragment = compile(GL_FRAGMENT_SHADER, fragmentSource);
            program = glCreateProgram();
            glAttachShader(program, vertex);
            glAttachShader(program, fragment);
            glLinkProgram(program);
            if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
                throw new IllegalStateException("Shader link failed:\n" + glGetProgramInfoLog(program));
            }
            id = program;
            program = 0; // Ownership transferred to this instance.
        } finally {
            if (program != 0) glDeleteProgram(program);
            if (fragment != 0) glDeleteShader(fragment);
            if (vertex != 0) glDeleteShader(vertex);
        }
    }

    private static int compile(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String stage = type == GL_VERTEX_SHADER ? "vertex" : "fragment";
            String log = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            throw new IllegalStateException(stage + " shader compilation failed:\n" + log);
        }
        return shader;
    }

    private static String readResource(String path) {
        try (InputStream stream = ShaderProgram.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Missing shader resource: " + path);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new IllegalStateException("Could not read shader resource: " + path, error);
        }
    }

    public void bind() {
        glUseProgram(id);
    }

    public int attribute(String name) {
        int location = glGetAttribLocation(id, name);
        if (location < 0) {
            throw new IllegalArgumentException("Shader attribute is not active: " + name);
        }
        return location;
    }

    public void setInt(String name, int value) {
        int location = uniform(name);
        if (location >= 0) glUniform1i(location, value);
    }

    public void setFloat(String name, float value) {
        int location = uniform(name);
        if (location >= 0) glUniform1f(location, value);
    }

    public void setVec2(String name, float x, float y) {
        int location = uniform(name);
        if (location >= 0) glUniform2f(location, x, y);
    }

    public void setMatrix4f(String name, Matrix4f matrix) {
        int location = uniform(name);
        if (location < 0) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(location, false, matrix.get(stack.mallocFloat(16)));
        }
    }

    private int uniform(String name) {
        return uniforms.computeIfAbsent(name, key -> glGetUniformLocation(id, key));
    }

    public int id() {
        return id;
    }

    @Override
    public void close() {
        glDeleteProgram(id);
        uniforms.clear();
    }
}
