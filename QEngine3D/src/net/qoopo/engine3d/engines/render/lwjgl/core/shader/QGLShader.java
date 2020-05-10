/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.lwjgl.core.shader;

//import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;




//import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
//import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
//import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
//import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
//import static org.lwjgl.opengl.GL20.glAttachShader;
//import static org.lwjgl.opengl.GL20.glCompileShader;
//import static org.lwjgl.opengl.GL20.glCreateProgram;
//import static org.lwjgl.opengl.GL20.glCreateShader;
//import static org.lwjgl.opengl.GL20.glDeleteProgram;
//import static org.lwjgl.opengl.GL20.glDetachShader;
//import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
//import static org.lwjgl.opengl.GL20.glGetProgrami;
//import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
//import static org.lwjgl.opengl.GL20.glGetShaderi;
//import static org.lwjgl.opengl.GL20.glLinkProgram;
//import static org.lwjgl.opengl.GL20.glShaderSource;
//import static org.lwjgl.opengl.GL20.glUseProgram;
//import static org.lwjgl.opengl.GL20.glValidateProgram;

/**
 *
 * @author alberto
 */
public class QGLShader {

    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;

    public QGLShader() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
