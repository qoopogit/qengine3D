package net.qoopo.engine3d.core.carga.impl.md5;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
//import javax.vecmath.Vector3f;
import net.qoopo.engine3d.engines.animacion.QAnimacionFrame;
import net.qoopo.engine3d.engines.animacion.QParAnimacion;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.animacion.QCompAlmacenAnimaciones;
import net.qoopo.engine3d.componentes.animacion.QComponenteAnimacion;
import net.qoopo.engine3d.componentes.animacion.QEsqueleto;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaConvexa;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.carga.impl.md5.util.MD5AnimModel;
import net.qoopo.engine3d.core.carga.impl.md5.util.MD5BaseFrame;
import net.qoopo.engine3d.core.carga.impl.md5.util.MD5Frame;
import net.qoopo.engine3d.core.carga.impl.md5.util.MD5Hierarchy;
import net.qoopo.engine3d.core.carga.impl.md5.util.MD5JointInfo;
import net.qoopo.engine3d.core.carga.impl.md5.util.MD5Mesh;
import net.qoopo.engine3d.core.carga.impl.md5.util.MD5Model;
import net.qoopo.engine3d.core.carga.impl.md5.util.MD5Utils;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.math.Cuaternion;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QRotacion;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.util.QJOMLUtil;
import net.qoopo.engine3d.core.util.Utils;
import org.joml.Vector3f;

public class MD5Loader {

    //Especificaciones tecnicas del formato del archivo 
//    http://tfc.duke.free.fr/coding/md5-specs-en.html
//    private static final String NORMAL_FILE_SUFFIX = "_normal";
    private static final String NORMAL_FILE_SUFFIX = "_local";

    /**
     * Cara un archivo con formato MD5 de DOOM3
     *
     * @param rutaArchivo
     * @return
     * @throws Exception
     */
    public static QEntidad cargar(String rutaArchivo) throws Exception {
        List<MD5AnimModel> animaciones = new ArrayList<>();
        File archivo = new File(rutaArchivo);
        MD5Model md5Meshodel = MD5Model.parse(archivo.getAbsolutePath());

        //animaciones
        File carpeta = archivo.getParentFile();
        for (File f : carpeta.listFiles()) {
            if (f.getName().toLowerCase().endsWith(".md5anim")) {
                animaciones.add(MD5AnimModel.parse(f.getAbsolutePath()));
            }
        }

        return MD5Loader.cargar(md5Meshodel, animaciones, QColor.WHITE, archivo.getParentFile());
    }

    /**
     * Crea una entidad a partir de un MD5Model. Agrega a la entidad los
     * componenetes del esqueleto, Geometr[ia y componentes de colision y
     * f[isicas estatiscas para cada Malla
     *
     * @param md5Model
     * @param animModels
     * @param defaultColour
     * @param rutaTrabajo
     * @return
     * @throws Exception
     */
    public static QEntidad cargar(MD5Model md5Model, List<MD5AnimModel> animModels, QColor defaultColour, File rutaTrabajo) throws Exception {
        QEntidad entidad = new QEntidad(md5Model.getNombre());
        QEsqueleto esqueleto = crearEsqueleto(md5Model);
        entidad.agregarComponente(esqueleto);

        //geometria
        for (MD5Mesh md5Mesh : md5Model.getMeshes()) {
            QGeometria geometria = generarGeometria(md5Mesh, esqueleto);
            cargarTexturas(geometria, md5Mesh, defaultColour, rutaTrabajo);
            entidad.agregarComponente(geometria);
            //****************** OBJETOS DE COLISION ********************************
            //agrega componentes fisicos para cada malla
            QFormaColision colision = new QColisionMallaConvexa(geometria);
            entidad.agregarComponente(colision);
            QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.ESTATICO, 0);
            rigido.setFormaColision(colision);
            entidad.agregarComponente(rigido);
        }

        //animaciones
        QCompAlmacenAnimaciones almacen = new QCompAlmacenAnimaciones();
        // agrego a la entidad el almacen de animaciones
        entidad.agregarComponente(almacen);
        almacen.agregarAnimacion("Pose_Inicial", QComponenteAnimacion.crearAnimacionPose(esqueleto));
        if (animModels != null && !animModels.isEmpty()) {
            for (MD5AnimModel animModel : animModels) {
                QComponenteAnimacion animacion = crearAnimacion(md5Model, animModel, esqueleto);
                almacen.agregarAnimacion(animModel.getNombre(), animacion);
            }

//            entidad.agregarComponente(almacen.getAnimacion("Pose"));
            if (almacen.getAnimacion(md5Model.getNombre()) != null) {
                //le setea la animacion con el mismo nombre del archivo para que se ejecute
                entidad.agregarComponente(almacen.getAnimacion(md5Model.getNombre()));
            } else {
                //le setea la primera animacion para q se ejecute                       
                entidad.agregarComponente(almacen.getAnimacion(animModels.get(0).getNombre()));
            }

        }

        return entidad;
    }

    /**
     * Crea un componenete de Esqueleto a partir de MD5Model
     *
     * @param md5Model
     * @return
     */
    private static QEsqueleto crearEsqueleto(MD5Model md5Model) {
        QEsqueleto esqueleto = new QEsqueleto();
        List<MD5JointInfo.MD5JointData> joints = md5Model.getJointInfo().getJoints();
        int numJoints = joints.size();
        esqueleto.setHuesos(new ArrayList<>());
        QHueso hueso;
        MD5JointInfo.MD5JointData joint;
        for (int i = 0; i < numJoints; i++) {
            joint = joints.get(i);
            hueso = new QHueso(i, joint.getName());
            hueso.setTransformacion(new QTransformacion(QRotacion.CUATERNION));
            hueso.getTransformacion().getTraslacion().set(QJOMLUtil.convertirQVector3(joint.getPosition()));
            hueso.getTransformacion().getRotacion().setCuaternion(joint.getOrientation().clone());
            //la inversa de la trasnformacion, la calculamos manualmente sin tomar en cuenta la jerarquia
            hueso.transformacionInversa = hueso.getTransformacion().toTransformMatriz().invert();
            if (joint.getParentIndex() > -1) {
                esqueleto.getHueso(joint.getParentIndex()).agregarHijo(hueso);
            }
            esqueleto.agregarHueso(hueso);
        }
//        esqueleto.calcularMatricesInversas();
        return esqueleto;
    }

    /**
     * Construye un componente de Geometria a partir de un MD5Model que contiene
     * la informaci[on de la malla
     *
     * @param md5Model
     * @param md5Mesh
     * @param esqueleto
     * @return
     * @throws Exception
     */
    private static QGeometria generarGeometria(MD5Mesh md5Mesh, QEsqueleto esqueleto) throws Exception {
        List<MD5Mesh.MD5Vertex> vertices = md5Mesh.getVertices();
        List<MD5Mesh.MD5Weight> weights = md5Mesh.getWeights();

        QGeometria geometria = new QGeometria();

        for (MD5Mesh.MD5Vertex vertex : vertices) {
            QVector3 posicionVertice = new QVector3();
            int pesoInicial = vertex.getStartWeight();
            int numeroPesos = vertex.getWeightCount();
            QHueso[] huesos = new QHueso[numeroPesos];
//            QVector3[] pesosPosiciones = new QVector3[numeroPesos];
            float[] pesos = new float[numeroPesos];
            int[] huesosIds = new int[numeroPesos];
            int c = 0;

            //-------------------------------
            /**
             * La posición del vértice se calcula usando todos los pesos
             * relacionados. Cada peso tiene una posición y un sesgo. La suma de
             * todos los sesgos de los pesos asociados a cada vértice debe ser
             * igual a 1.0. Cada peso también tiene una posición que se define
             * en el espacio local de la articulación, por lo que debemos
             * transformarlo en coordenadas de espacio modelo utilizando la
             * orientación y la posición de la articulación (como si se tratara
             * de una matriz de transformación) a la que se refiere.
             */
            for (int indicePeso = pesoInicial; indicePeso < pesoInicial + numeroPesos; indicePeso++) {
                MD5Mesh.MD5Weight weight = weights.get(indicePeso);
                huesos[c] = esqueleto.getHueso(weight.getJointIndex());
                huesosIds[c] = weight.getJointIndex();
                pesos[c] = weight.getBias();
//                pesosPosiciones[c] = QJOMLUtil.convertirQVector3(weight.getPosition());

                //------------------------------------------------------------------------------
                //de acuerdo a lo q entendi en el documento de donde tome este codigo para cargar modelos MD5
                //https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter19/chapter19.html
                //toma la transformacion del hueso que ha calculado antes en la creacion del hueso
                //usa la transfomación sin tomar en cuenta a los padres porq el peso tiene la posición en el espacio de coordenadas de la articulación
                posicionVertice.add(huesos[c].getTransformacion().toTransformMatriz().mult(QJOMLUtil.convertirQVector3(weight.getPosition())).multiply(weight.getBias()));
                c++;
            }
            QVertice vertice = geometria.agregarVertice(posicionVertice.x, posicionVertice.y, posicionVertice.z, vertex.getTextCoords().x, 1.0f - vertex.getTextCoords().y);
            vertice.setListaHuesos(huesos);
            vertice.setListaHuesosPesos(pesos);
            vertice.setListaHuesosIds(huesosIds);
//            vertice.setListaPosicionPesos(pesosPosiciones);
        }

        for (MD5Mesh.MD5Triangle tri : md5Mesh.getTriangles()) {
            geometria.agregarPoligono(tri.getVertex0(), tri.getVertex2(), tri.getVertex1());
        }

        geometria = QUtilNormales.calcularNormales(geometria);
        geometria = QMaterialUtil.suavizar(geometria, true);
//        geometria = QUtilNormales.invertirNormales(geometria);
        return geometria;
    }

    private static void cargarTexturas(QGeometria mesh, MD5Mesh md5Mesh, QColor defaultColour, File rutaTrabajo) throws Exception {

        String texturePath = md5Mesh.getTexture();
        if (texturePath == null) {
            return;
        }
        //si es ruta completa
        if (new File(texturePath).exists()) {
            //todo ok
        } else {
            //compruebo si es una ruta relativa
            if (new File(rutaTrabajo, texturePath).exists()) {
                texturePath = new File(rutaTrabajo, texturePath).getAbsolutePath();
            }
        }

        try {
            if (texturePath != null && texturePath.length() > 0) {
                if (new File(texturePath).exists()) {
                    QTextura textura = QGestorRecursos.cargarTextura(texturePath, texturePath);
//            QProcesadorTextura textura = new QProcesadorSimple(text);            
                    QMaterialBas material = new QMaterialBas(textura);

                    // Handle normal Maps;
                    int pos = texturePath.lastIndexOf(".");
                    if (pos > 0) {
                        String basePath = texturePath.substring(0, pos);
                        String extension = texturePath.substring(pos, texturePath.length());
                        String normalMapFileName = basePath + NORMAL_FILE_SUFFIX + extension;
//                System.out.println("el archivo de normal deberia ser:" + normalMapFileName);
                        if (Utils.existsResourceFile(normalMapFileName)) {
                            QTextura normalMap = QGestorRecursos.cargarTextura(normalMapFileName, normalMapFileName);
                            material.setMapaNormal(new QProcesadorSimple(normalMap));
                        }
                    }
                    QMaterialUtil.aplicarMaterial(mesh, material);

                } else {
//            mesh.setMaterial(new Material(defaultColour, 1));
                    QMaterialUtil.aplicarColor(mesh, 1, defaultColour, QColor.WHITE, 0, 64);
                }
            }
        } catch (Exception e) {

        }
    }

    private static QComponenteAnimacion crearAnimacion(MD5Model md5Model, MD5AnimModel animModel, QEsqueleto esqueleto) {

        float duracionFrames = 1.0f / animModel.getHeader().getFrameRate();
//        float duracionAnimacion = duracionFrames * animModel.getHeader().getNumFrames();
        float duracionAnimacion = (float) animModel.getHeader().getNumFrames() / (float) animModel.getHeader().getFrameRate();

        //calcula la duracion de cada frame de acuerdo a la duracion de la animacion / el numero de frames
//        duracionFrames = duracionAnimacion / animModel.getHeader().getNumFrames();
//        System.out.println("Animación:" + animModel.getNombre());
//        System.out.println("FrameRate=" + animModel.getHeader().getFrameRate());
//        System.out.println("Duracion de la animacion=" + duracionAnimacion);
//        QComponenteAnimacion animacion = new QComponenteAnimacion();
        QComponenteAnimacion animacion = new QComponenteAnimacion(duracionAnimacion);
        animacion.setNombre(animModel.getNombre());
        animacion.setLoop(true);
//        animacion.setLoop(false);

        List<MD5Frame> frames = animModel.getFrames();

        float segundos = 0.0f;
        for (MD5Frame md5Frame : frames) {
            QAnimacionFrame frame = processAnimationFrame(md5Model, animModel, md5Frame, esqueleto, segundos);
            segundos += duracionFrames;
            animacion.agregarAnimacionFrame(frame);
        }
        return animacion;
    }

    /**
     *
     * @param md5Model
     * @param animModel
     * @param frame
     * @param esqueleto
     * @param tiempo
     * @return
     */
    private static QAnimacionFrame processAnimationFrame(MD5Model md5Model, MD5AnimModel animModel, MD5Frame frame, QEsqueleto esqueleto, float tiempo) {
        //crea un frame para el tipo de animacion por frames
//        QAnimacionFrame qFrame = new QAnimacionFrame(QAnimacionFrame.TIPO_FINITA, 1);
        QAnimacionFrame qFrame = new QAnimacionFrame(tiempo);

        MD5BaseFrame baseFrame = animModel.getBaseFrame();
        List<MD5Hierarchy.MD5HierarchyData> hierarchyList = animModel.getHierarchy().getHierarchyDataList();
        List<MD5JointInfo.MD5JointData> joints = md5Model.getJointInfo().getJoints();
        int numJoints = joints.size();
        float[] frameData = frame.getFrameData();

        QEntidad hueso;
        QTransformacion transformacion;
        for (int i = 0; i < numJoints; i++) {
            hueso = esqueleto.getHueso(i);
            MD5BaseFrame.MD5BaseFrameData baseFrameData = baseFrame.getFrameDataList().get(i);
            Vector3f position = baseFrameData.getPosition();
            Cuaternion orientation = baseFrameData.getOrientation().clone();

            int flags = hierarchyList.get(i).getFlags();
            int startIndex = hierarchyList.get(i).getStartIndex();

            if ((flags & 1) > 0) {
                position.x = frameData[startIndex++];
            }
            if ((flags & 2) > 0) {
                position.y = frameData[startIndex++];
            }
            if ((flags & 4) > 0) {
                position.z = frameData[startIndex++];
            }
            if ((flags & 8) > 0) {
                orientation.x = frameData[startIndex++];
            }
            if ((flags & 16) > 0) {
                orientation.y = frameData[startIndex++];
            }
            if ((flags & 32) > 0) {
                orientation.z = frameData[startIndex++];
            }
            // Update Quaternion's w component
            orientation = MD5Utils.calculateQuaternion(new Vector3f(orientation.x, orientation.y, orientation.z));
            transformacion = new QTransformacion(QRotacion.CUATERNION);
            transformacion.getTraslacion().set(position.x, position.y, position.z);
            transformacion.getRotacion().setCuaternion(orientation.clone());

            // para probar, la multiplico por la inversa de la transformacion del hueso
//            QMatriz4 mat = transformacion.toTransformMatriz();            
//            QMatriz4 matInversa = hueso.transformacion.toTransformMatriz();
//            matInversa = matInversa.invert();
//            mat.multLocal(matInversa);
//            transformacion.desdeMatrix(mat);
            qFrame.agregarPar(new QParAnimacion(hueso, transformacion));
        }
        return qFrame;
    }

}
