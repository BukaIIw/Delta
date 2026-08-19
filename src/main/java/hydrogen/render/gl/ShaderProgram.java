package hydrogen.render.gl;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL45.*;

public final class ShaderProgram implements AutoCloseable {
    private final int id;

    public ShaderProgram(String vertexSource, String fragmentSource) {
        int vertex = compile(GL_VERTEX_SHADER, vertexSource);
        int fragment = compile(GL_FRAGMENT_SHADER, fragmentSource);
        id = glCreateProgram();
        glAttachShader(id, vertex);
        glAttachShader(id, fragment);
        glLinkProgram(id);
        if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(id);
            glDeleteShader(vertex);
            glDeleteShader(fragment);
            glDeleteProgram(id);
            throw new IllegalStateException("Shader link failed: " + log);
        }
        glDetachShader(id, vertex);
        glDetachShader(id, fragment);
        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    private static int compile(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            throw new IllegalStateException("Shader compile failed: " + log);
        }
        return shader;
    }

    public void bind() { glUseProgram(id); }

    public void setMatrix4f(String name, Matrix4f matrix) {
        int location = glGetUniformLocation(id, name);
        if (location < 0) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glProgramUniformMatrix4fv(id, location, false, matrix.get(stack.mallocFloat(16)));
        }
    }

    public int id() { return id; }

    @Override
    public void close() { glDeleteProgram(id); }
}
