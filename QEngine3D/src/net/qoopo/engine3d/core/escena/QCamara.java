/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.escena;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.math.QVector2;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QClipPane;

/**
 * La cámara
 *
 * @author aigarcia
 */
public class QCamara extends QEntidad {

    public static final QGeometria GEOMETRIA_CAM = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/camara/camara.obj")).get(0));
    public static final QGeometria GEOMETRIA_CAM_1 = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/camara/camara.obj")).get(1));
    private static final QGeometria GEOMETRIA_FRUSTUM = new QGeometria();
    private float escalaOrtogonal = 0.0f;

    private QMaterialBas material;

    //ancho de la superficie 
//    public int pantallaAncho = 0;
//    public int pantallaAlto = 0;
    private boolean ortogonal = false;
    private float radioAspecto = 800.0f / 600.0f;
    public float camaraAlto;
    public float camaraAncho;

    /**
     * Angulo de Vision de la Camara , Los humanos tenemos un angulo de vision
     * de 60 grados
     */
    private float FOV = (float) Math.toRadians(60.0f);

    //---------------------- datos para el procesos con matrices ----------------------------
    public float frustrumCerca;
    /**
     * Distance from camera to far frustum plane.
     */
    public float frustrumLejos;
    /**
     * Distance from camera to left frustum plane.
     */
    public float frustumIzquierda;
    /**
     * Distance from camera to right frustum plane.
     */
    public float frustumDerecha;
    /**
     * Distance from camera to top frustum plane.
     */
    public float frustumArriba;
    /**
     * Distance from camera to bottom frustum plane.
     */
    public float frustumAbajo;

    /**
     * Los planos de recorte del frustum de la camara
     */
    private final QClipPane[] planosRecorte = new QClipPane[6];

    /**
     * Matriz para aplicar la proyeccion
     */
    private final QMatriz4 matrizProyeccion = new QMatriz4();

    public QCamara() {
        super("Cam ", false);
        iniciar();
        agregarComponente(GEOMETRIA_CAM);
        agregarComponente(GEOMETRIA_CAM_1);
        agregarComponente(GEOMETRIA_FRUSTUM);
    }

    public QCamara(String name) {
        super(name, false);
        iniciar();
        agregarComponente(GEOMETRIA_CAM);
        agregarComponente(GEOMETRIA_CAM_1);
        agregarComponente(GEOMETRIA_FRUSTUM);
    }

    public void iniciar() {
        transformacion.setTraslacion(QVector3.zero.clone());
        transformacion.getRotacion().inicializar();
        escalaOrtogonal = 1.0f;
        frustrumCerca = 1.0f;
        frustrumLejos = 100f;
        material = new QMaterialBas();
        material.setColorDifusa(new QColor(1, 1, 204.0f / 255.0f));
        material.setTransAlfa(0.3f);
        material.setTransparencia(true);
//        material.setFactorEmision(0.15f);
        actualizarCamara();
    }

    /**
     * Comprueba que el punto ubicado en x,y,z, se encuentre en el campo de
     * visi[on de la camara
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean estaEnCampoVision(float x, float y, float z) {
        boolean visible = true;
        TempVars t = TempVars.get();
        t.vector3f1.setXYZ(x, y, z);
//        visible = (planosRecorte[0].distancia(t.vector3f1) > 0.005f);
//        visible = (planosRecorte[1].distancia(t.vector3f1) > 0.005f);
        for (int i = 0; i < 2; i++) { //solo far plane y near plane
//        for (int i = 0; i < 6; i++) {
            if (!this.planosRecorte[i].esVisible(t.vector3f1)) {
//            if (!(this.planosRecorte[i].distancia(t.vector3f1) > 0.005f)) {
                visible = false;
            }
        }
        t.release();
        return visible;
    }

    /**
     * Obtiene el alfa (factor de interpolacion) para obtener el vertice
     * recortado
     *
     * @param v1
     * @param v2
     * @return
     */
    public float obtenerClipedVerticeAlfa(QVector3 v1, QVector3 v2) {
//        TempVars t = TempVars.get();
        float alfa = 0.0f;
//        t.vector3f1.set(v1);
        for (int i = 0; i < 2; i++) { //solo far plane y near plane
//        for (int i = 0; i < 6; i++) {
            if (!this.planosRecorte[i].esVisible(v1) || !this.planosRecorte[i].esVisible(v2)) {
//            if (!(this.planosRecorte[i].distancia(t.vector3f1) > 0.005f)) {
                float da = this.planosRecorte[i].distancia(v1);   // distance plane -> point a
                float db = this.planosRecorte[i].distancia(v2);   // distance plane -> point b
                return da / (da - db);   // intersection factor (between 0 and 1)
            }
        }
//        t.release();
        return alfa;
    }

    public boolean estaEnCampoVision(QVertice vertice) {
        try {
            return estaEnCampoVision(vertice.ubicacion);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @param vertice
     * @return
     */
    public boolean estaEnCampoVision(QVector3 vertice) {
        return estaEnCampoVision(vertice.x, vertice.y, vertice.z);
    }

    /**
     *
     * @param vertice
     * @return
     */
    public boolean estaEnCampoVision(QVector4 vertice) {
        return estaEnCampoVision(vertice.x, vertice.y, vertice.z);
    }

    public void configurarRadioAspecto(int pantallaAncho, int pantallaAlto) {
        radioAspecto = (float) pantallaAncho / pantallaAlto;
        actualizarCamara();
    }

    /**
     * Actualiza los valores de frustrum de la cámara de acuerdo al ángulo FOV y
     * actualiza la matriz de proyeccion
     */
    public void actualizarCamara() {
        camaraAlto = 2 * (float) Math.tan(FOV / 2);
        camaraAncho = camaraAlto * radioAspecto;
        frustumIzquierda = -camaraAncho / 2;
        frustumDerecha = camaraAncho / 2;
        frustumArriba = -camaraAlto / 2;
        frustumAbajo = camaraAlto / 2;
        if (ortogonal) {
            frustumArriba *= escalaOrtogonal;
            frustumDerecha *= escalaOrtogonal;
            frustumIzquierda *= escalaOrtogonal;
            frustumAbajo *= escalaOrtogonal;
        }
        construirMatrizProyeccion();
        construirGeometria();
    }

    /**
     * Calcula las esquinas del tronco frutum con coordenadas en el espacio de
     * la camara (matriz de trasnformacion inversa)
     *
     * @return
     * https://stackoverflow.com/questions/13665932/calculating-the-viewing-frustum-in-a-3d-space
     *
     */
    public QVector3[] getEsquinasFrustum() {
        QVector3[] esquinas = new QVector3[8];
        //la matriz de la matriz la invertimos para convertir a las coordenadas en el espacio de la camara
//        QMatriz4 matrizCamara = getMatrizTransformacion(System.currentTimeMillis());
//        QVector3 centro = matrizCamara.toTranslationVector();
//        QVector3 camaraDireccion = matrizCamara.toRotationQuat().getRotationColumn(2);//el frente de la camara en funcion de sus padres tambien
//        QVector3 camaraArriba = matrizCamara.toRotationQuat().getRotationColumn(1);//el frente de la camara en funcion de sus padres tambien
//        QVector3 camaraIzquierda = matrizCamara.toRotationQuat().getRotationColumn(0);//el frente de la camara en funcion de sus padres tambien

        //en coordenadas de la camara
        QVector3 centro = new QVector3();
        QVector3 camaraDireccion = new QVector3(0, 0, -1.0f);
        QVector3 camaraArriba = new QVector3(0, 1.0f, 0);
        QVector3 camaraIzquierda = new QVector3(-1.0f, 0, 0);
        //-----------------
        QVector3 centroCerca = centro.clone().add(camaraDireccion.clone().normalize().multiply(frustrumCerca));
        QVector3 centroLejos = centro.clone().add(camaraDireccion.clone().normalize().multiply(frustrumLejos));

        float cercaAlto = 2 * (float) Math.tan(FOV / 2) * frustrumCerca;
//        float cercaAncho = cercaAlto * pantallaAncho / pantallaAlto;
        float cercaAncho = cercaAlto * radioAspecto;

        float lejosAlto = 2 * (float) Math.tan(FOV / 2) * frustrumLejos;
//        float lejosAncho = lejosAlto * pantallaAncho / pantallaAlto;
        float lejosAncho = lejosAlto * radioAspecto;

        if (ortogonal) {
            lejosAncho = cercaAncho;
            lejosAlto = cercaAlto;
        }

        //lejanas
        //superior izquierda
        esquinas[0] = centroLejos.clone().add(camaraArriba.clone().multiply(lejosAlto * (-frustumArriba)).add(camaraIzquierda.clone().multiply(lejosAncho * (-frustumIzquierda))));
        //superior derecha
        esquinas[1] = centroLejos.clone().add(camaraArriba.clone().multiply(lejosAlto * (-frustumArriba)).add(camaraIzquierda.clone().multiply(lejosAncho * (-frustumDerecha))));
        //inferiro izquierda
        esquinas[2] = centroLejos.clone().add(camaraArriba.clone().multiply(-lejosAlto * (frustumAbajo)).add(camaraIzquierda.clone().multiply(lejosAncho * (-frustumIzquierda))));
        //inferior derecha
        esquinas[3] = centroLejos.clone().add(camaraArriba.clone().multiply(-lejosAlto * (frustumAbajo)).add(camaraIzquierda.clone().multiply(lejosAncho * (-frustumDerecha))));

        //----------cercanas
        //superior izquierda
        esquinas[4] = centroCerca.clone().add(camaraArriba.clone().multiply(cercaAlto * (-frustumArriba)).add(camaraIzquierda.clone().multiply(cercaAncho * (-frustumIzquierda))));
        //superior derecha
        esquinas[5] = centroCerca.clone().add(camaraArriba.clone().multiply(cercaAlto * (-frustumArriba)).add(camaraIzquierda.clone().multiply(cercaAncho * (-frustumDerecha))));
        //inferiro izquierda
        esquinas[6] = centroCerca.clone().add(camaraArriba.clone().multiply(-cercaAlto * (frustumAbajo)).add(camaraIzquierda.clone().multiply(cercaAncho * (-frustumIzquierda))));
        //inferior derecha
        esquinas[7] = centroCerca.clone().add(camaraArriba.clone().multiply(-cercaAlto * (frustumAbajo)).add(camaraIzquierda.clone().multiply(cercaAncho * (-frustumDerecha))));

        return esquinas;
    }

    /**
     * Devuelve la matriz de proyeccion de la camara
     *
     * @return
     */
    public QMatriz4 getMatrizProyeccion() {
        return matrizProyeccion;
    }

    /**
     * Matriz de proyeccion igua a la de OpenGL
     * http://www.songho.ca/opengl/gl_projectionmatrix.html
     *
     */
    private void construirMatrizProyeccion() {
        matrizProyeccion.fromFrustum(frustrumCerca, frustrumLejos, frustumIzquierda, frustumDerecha, frustumArriba, frustumAbajo, ortogonal);
        construirPlanosRecorte();
    }

    private void construirPlanosRecorte() {
        QVector3[] esquinas = getEsquinasFrustum();
        //Plano cercano
        planosRecorte[0] = new QClipPane(esquinas[6], esquinas[4], esquinas[5]);
        //Plano lejano
        planosRecorte[1] = new QClipPane(esquinas[2], esquinas[1], esquinas[0]);
        //Plano superior
        planosRecorte[2] = new QClipPane(esquinas[0], esquinas[5], esquinas[4]);
        //Plano inferior
        planosRecorte[3] = new QClipPane(esquinas[6], esquinas[7], esquinas[2]);
        //Plano derecha
        planosRecorte[4] = new QClipPane(esquinas[7], esquinas[5], esquinas[1]);
        //Plano izquierda
        planosRecorte[5] = new QClipPane(esquinas[6], esquinas[2], esquinas[4]);
    }

    /**
     * Construye una geometria para ver la camara
     */
    private void construirGeometria() {
        try {
            QVector3[] esquinas = getEsquinasFrustum();
            GEOMETRIA_FRUSTUM.destroy();
            GEOMETRIA_FRUSTUM.listaVertices = new QVertice[0];
            GEOMETRIA_FRUSTUM.listaPrimitivas = new QPrimitiva[0];
            for (QVector3 vector : esquinas) {
                GEOMETRIA_FRUSTUM.agregarVertice(vector);
            }

            //cercano
            GEOMETRIA_FRUSTUM.agregarPoligono(material, 4, 6, 7, 5);
            //lejano
            GEOMETRIA_FRUSTUM.agregarPoligono(material, 1, 3, 2, 0);
            //superior
            GEOMETRIA_FRUSTUM.agregarPoligono(material, 0, 4, 5, 1);
            //inferior
            GEOMETRIA_FRUSTUM.agregarPoligono(material, 2, 3, 7, 6);
            //derecha
            GEOMETRIA_FRUSTUM.agregarPoligono(material, 7, 3, 1, 5);
            //izquierda
            GEOMETRIA_FRUSTUM.agregarPoligono(material, 0, 2, 6, 4);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void construirMatrizProyeccion(float frustrumCerca, float frustrumLejos, float frustrumIzquierda, float frustrumDerecha, float frustumArriba, float frustumAbajo, boolean orthogonal) {
        this.frustrumCerca = frustrumCerca;
        this.frustrumLejos = frustrumLejos;
        this.frustumIzquierda = frustrumIzquierda;
        this.frustumDerecha = frustrumDerecha;
        this.frustumArriba = frustumArriba;
        this.frustumAbajo = frustumAbajo;
        this.ortogonal = orthogonal;
        construirMatrizProyeccion();
    }

    /**
     * Aplica la proyeccion, al multiplicar por la matriz de proyeccion y
     * realiza la division para la perspectiva
     *
     *
     * @param vector
     * @return vector en coordenadas normalizadas (-1:1)
     */
    public QVector3 proyectar(QVector4 vector) {
        //metodo matricial
        //pasos
        // http://www.songho.ca/opengl/gl_transform.html
        // Los datos ya vienen en coordenads del Ojo porq se multiplicaron por la inversa de la transformación de la cámara
        // 1.1 Pasar a coordenadas del Clip  usando la matriz de proyeccion (Usa la matriz proyectar u ortogonal y realiza el clipping )
        // Eso devuelve coordenadas en el espacio homogeneo

        TempVars tmp = TempVars.get();
        try {
            //Primero proyeccion en proyectar esta la deja en coordenadas homogeneas (de-1 a 1)
            QVector4 r1 = getMatrizProyeccion().mult(vector);
            //1.2 Ahora vamos a normalizar las coordendas dividiendo para el componente W (Perspectiva)
            tmp.vector3f2.setXYZ(r1.x / r1.w, r1.y / r1.w, r1.z / r1.w); //division de perspertiva
        } finally {
            tmp.release();
        }
        return tmp.vector3f2;
    }

    /**
     * Devuelve las coordenadas de la pantalla de coordenadas ya normalizadas
     *
     * @param vector de coordenadas normalizadas
     * @return
     */
    private QVector3 coordenadasPantalla(QVector3 vector, int pantallaAncho, int pantallaAlto) {
        //metodo matricial
        // http://www.songho.ca/opengl/gl_transform.html
        TempVars tmp = TempVars.get();
        try {
            tmp.vector3f1.setXYZ(
                    (vector.x * pantallaAncho + pantallaAncho) / 2,
                    (vector.y * pantallaAlto + pantallaAlto) / 2,
                    //                    vector.z * (frustrumLejos - frustrumCerca) / 2 + (frustrumCerca + frustrumLejos) / 2 //este valor ya no se utiliza, ya no lo calculo
                    0
            );
        } finally {
            tmp.release();
        }
        return tmp.vector3f1;
    }

    /**
     * Convierte las coordenadas del espacio de la Camara a coordenadas de la
     * pantalla
     *
     * @param onScreen
     * @param vector
     * @param pantallaAncho
     * @param pantallaAlto
     */
    public void getCoordenadasPantalla(QVector2 onScreen, QVector4 vector, int pantallaAncho, int pantallaAlto) {
        QVector3 tmp2 = coordenadasPantalla(proyectar(vector), pantallaAncho, pantallaAlto);
        if (tmp2 != null) {
            onScreen.x = (int) tmp2.x;
            onScreen.y = (int) tmp2.y;
//            return tmp2.xy();
        }
//        return null;
    }

    @Override
    public QCamara clone() {
        QCamara newCamara = new QCamara();
        newCamara.setTransformacion(transformacion.clone());
        return newCamara;
    }

    public void lookAtPosicionObjetivo(QVector3 posicion, QVector3 posicionObjetivo, QVector3 vectorArriba) {
        lookAt(posicion.clone(), posicion.clone().add(posicionObjetivo.clone().multiply(-1)), vectorArriba);
    }

    /**
     * Actualiza la posicion y direccion de la camara
     *
     * @param posicion
     * @param direccion
     * @param vectorArriba
     */
    public void lookAt(QVector3 posicion, QVector3 direccion, QVector3 vectorArriba) {
        transformacion.setTraslacion(posicion);
        transformacion.getRotacion().getCuaternion().lookAt(direccion, vectorArriba);
        transformacion.getRotacion().actualizarAngulos();
        actualizarCamara();
    }

    public boolean isOrtogonal() {
        return ortogonal;
    }

    public void setOrtogonal(boolean ortogonal) {
        this.ortogonal = ortogonal;
//        if (ortogonal) {
//            escalaOrtogonal = (QMath.RAD_TO_DEG * FOV) / 5.4143f;
////            escalaOrtogonal = (QMath.RAD_TO_DEG * FOV) / 5.4143f;
////            System.out.println("Escala ortogonal =" + escalaOrtogonal);
////        } else {
////            FOV = QMath.DEG_TO_RAD * (escalaOrtogonal * 5.4143f);
//        }
        actualizarCamara();
    }

    public float getFOV() {
        return FOV;
    }

    public void setFOV(float FOV) {
        this.FOV = FOV;
        actualizarCamara();
    }

    public float getEscalaOrtogonal() {
        return escalaOrtogonal;
    }

    public void setEscalaOrtogonal(float escalaOrtogonal) {
        this.escalaOrtogonal = escalaOrtogonal;
        actualizarCamara();
    }

}
