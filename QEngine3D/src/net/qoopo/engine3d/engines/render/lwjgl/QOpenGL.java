/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.lwjgl;

//import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
//import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import java.nio.FloatBuffer;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.util.Utils;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.lwjgl.core.QGlVentana;
import net.qoopo.engine3d.engines.render.lwjgl.core.shader.QGLShader;
import net.qoopo.engine3d.engines.render.superficie.Superficie;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author alberto
 */
public class QOpenGL extends QMotorRender {

    private QGlVentana ventana;

    private int vboId;
    private int vaoId;
    private QGLShader shaderProgram;

    public QOpenGL(QEscena universo, Superficie superficie, int ancho, int alto) {
        super(universo, superficie, ancho, alto);
    }

    public QOpenGL(QEscena universo, String nombre, Superficie superficie, int ancho, int alto) {
        super(universo, nombre, superficie, ancho, alto);
    }

    @Override
    public void iniciar() {
        try {
            ventana = new QGlVentana(nombre, opciones.ancho, opciones.alto, true);
            ventana.init();
            ventana.setClearColor(colorFondo.r, colorFondo.g, colorFondo.b, 0.0f);

//            superficie.getComponente().getParent().add(canvas3D);
//            superficie.getComponente().getParent().remove(superficie.getComponente());
            shaderProgram = new QGLShader();
            shaderProgram.createVertexShader(Utils.loadResource("/net/qoopo/engine3d/engines/render/lwjgl/core/shader/vertice/vertex.vs"));
            shaderProgram.createFragmentShader(Utils.loadResource("/net/qoopo/engine3d/engines/render/lwjgl/core/shader/fragmento/fragmento.fs"));
            shaderProgram.link();

            //datos del triángulo a dibujar
            float[] vertices = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
            };

            // los convierte en un buffer de vértices para que pueda ser interpretado por OpenGL
            FloatBuffer verticesBuffer = null;
            try {
                verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
                verticesBuffer.put(vertices).flip();

                // Create the VAO and bind to it
                vaoId = glGenVertexArrays();
                glBindVertexArray(vaoId);

                // Create the VBO and bint to it
                vboId = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
                // Define structure of the data
                glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

                // desvincula the VBO
                glBindBuffer(GL_ARRAY_BUFFER, 0);

                // desvincula the VAO
                glBindVertexArray(0);
            } finally {
                if (verticesBuffer != null) {
                    MemoryUtil.memFree(verticesBuffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void detener() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }

        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public long update() {
        long ti;
        tiempoPrevio = System.currentTimeMillis();

//        ventana.setClearColor(colorFondo.r, colorFondo.g, colorFondo.b, 0.0f);
        clear();

        if (ventana.isResized()) {
            glViewport(0, 0, ventana.getWidth(), ventana.getHeight());
            ventana.setResized(false);
        }

        shaderProgram.bind();

        // Bind to the VAO (dice que se va a dibujar el triangulo que se definio al inicio)
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);

        // dibuja los vértice que están vinculados al vaoID
        glDrawArrays(GL_TRIANGLES, 0, 3);

        // Restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shaderProgram.unbind();

        ventana.update();
//        System.out.println("render: " + nombre + " renderizado t=" + tiempoPrevio);
        return tiempoPrevio;
    }

//    @Override
//    public void dibujarPixel(int x, int y) {
//    }

}
