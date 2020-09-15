package net.qoopo.engine3d.core.carga.impl.assimp.imp;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QComponente;
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
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.material.QMaterial;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.Cuaternion;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.math.QRotacion;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorInvierte;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.engines.animacion.QAnimacionFrame;
import net.qoopo.engine3d.engines.animacion.QParAnimacion;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AICamera;
import org.lwjgl.assimp.AIColor3D;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AILight;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;

/**
 * Permite la carga de vaiors formatos de modelos 3D usando la librer[ia ASSIMP.
 *
 * @author alberto
 */
public class AssimpLoader {

    /**
     * Carga los items dentro del archivo file
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static List<QEntidad> cargarAssimpItems(File file) throws Exception {
        return cargarAssimpItems(file.getAbsolutePath(), file.getParent());
    }

    /**
     * Carga los items dentro del archivo resourcePath
     *
     * @param resourcePath
     * @param texturesDir
     * @return
     * @throws Exception
     */
    public static List<QEntidad> cargarAssimpItems(String resourcePath, String texturesDir) throws Exception {
//        return AssimpLoader.cargarAssimpItems(resourcePath, texturesDir, Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_LimitBoneWeights);
        return AssimpLoader.cargarAssimpItems(resourcePath, texturesDir, Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate);
    }

    /**
     * Carga un archivo de modelos 3d y obtiene las entidades dentro del archivo
     *
     * @param rutaArchivo
     * @param texturesDir
     * @param flags
     * @return
     * @throws Exception
     */
    public static List<QEntidad> cargarAssimpItems(String rutaArchivo, String texturesDir, int flags) throws Exception {
        System.out.println("Cargando Recurso con ASSIMP");

        File f = new File(rutaArchivo);
        if (!f.exists()) {
            System.out.println("Archivo no existe");
        }

        AIScene aiScene = aiImportFile(rutaArchivo, flags);

        if (aiScene == null) {
            throw new Exception("Error al cargar escena");
        }

        System.out.println("    Mallas:" + aiScene.mNumMeshes());
        System.out.println("    Animaciones:" + aiScene.mNumAnimations());
        System.out.println("    Camaras:" + aiScene.mNumCameras());
        System.out.println("    Luces:" + aiScene.mNumLights());
        System.out.println("    Materiales:" + aiScene.mNumMaterials());
        System.out.println("    Texturas:" + aiScene.mNumTextures());

        List<QEntidad> entidades = new ArrayList<>();
        AINode aiNodoRaiz = aiScene.mRootNode();
        Node nodoRaiz = procesarJerarquiaNodos(aiNodoRaiz, null);
        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        Map<Integer, QMaterial> mapaMateriales = new HashMap();
        Integer indice = 0;
        for (int i = 0; i < numMaterials; i++) {
//            indice = aiMaterials.get(i);
            indice = i;
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            mapaMateriales.put(indice, procesarMaterial(aiMaterial, texturesDir));
        }
        PointerBuffer aiMeshes = aiScene.mMeshes();
//        PointerBuffer aiCameras = aiScene.mCameras();
//        PointerBuffer aiLights = aiScene.mLights();
//        PointerBuffer aiAnimations = aiScene.mAnimations();
//        PointerBuffer aiTextures = aiScene.mTextures();

        entidades.add(procesarNodo(aiScene, aiNodoRaiz, nodoRaiz, aiMeshes, mapaMateriales));

        return entidades;
    }

    /**
     * Cada aiNodeAnim contiene los estados de la animacion (Frames) de un solo
     * hueso
     *
     * @param aiNodeAnim
     * @param boneList
     * @param nodoRaiz
     * @param rootTransformation
     * @param duracionTotalAnimacion
     * @param esqueleto
     * @return
     */
    private static List<QAnimacionFrame> contruirFrames(AINodeAnim aiNodeAnim, float duracionTotalAnimacion, QEsqueleto esqueleto, QEntidad objeto) {
        int numFrames = aiNodeAnim.mNumPositionKeys();
        //Cada frame deberia tener configurado el tiempo, sin embargo al no encontrarlo asignamos equitativamente el tiempo para cada frame
        float duracion = (float) duracionTotalAnimacion / numFrames;
        List<QAnimacionFrame> frameList = new ArrayList<>();

        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        for (int iFrame = 0; iFrame < numFrames; iFrame++) {
            QAnimacionFrame qFrame = new QAnimacionFrame(duracion * iFrame);
            QEntidad hueso = null;

            if (esqueleto != null) {
                hueso = esqueleto.getHueso(aiNodeAnim.mNodeName().dataString());
            } else {
                if (objeto.getNombre().contains(aiNodeAnim.mNodeName().dataString())) {
                    hueso = objeto;
                }
            }

            if (hueso != null) {
                QTransformacion transformacion = new QTransformacion(QRotacion.CUATERNION);
                if (positionKeys != null && positionKeys.hasRemaining()) {
                    try{
//                    AIVectorKey aiVecKey = ;
                    AIVector3D vec = positionKeys.get(iFrame).mValue();
                    transformacion.getTraslacion().set(vec.x(), vec.y(), vec.z());
                    if (iFrame < aiNodeAnim.mNumScalingKeys()) {
                        if (scalingKeys != null && scalingKeys.hasRemaining()) {
//                            aiVecKey = scalingKeys.get(i);
                            vec = scalingKeys.get(iFrame).mValue();
                            transformacion.getEscala().set(vec.x(), vec.y(), vec.z());
                        }
                    }
                    } catch (Exception e) {
                        System.out.println("error cargando posicion y escala, no transformacion para el frame solicitado");
                    }
                }
                if (rotationKeys != null && rotationKeys.hasRemaining()) {
                    try {
                        AIQuatKey quatKey = rotationKeys.get(iFrame);
                        AIQuaternion aiQuat = quatKey.mValue();
                        Cuaternion quat = new Cuaternion(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
                        transformacion.getRotacion().setCuaternion(quat);
                    } catch (Exception e) {
                        System.out.println("error cargando rotacion, no transformacion para el frame solicitado");
                    }
                }

                qFrame.agregarPar(new QParAnimacion(hueso, transformacion));
                frameList.add(qFrame);
            }
        }

        return frameList;
    }

    /**
     * Crea un hueso para el esqueleto
     *
     * @param id
     * @param nodo
     * @param esqueleto
     * @param maxId
     * @return
     */
    private static QHueso crearHueso(int id, Node nodo, QEsqueleto esqueleto, Integer maxId, Integer contadorNodosSinPadre) {
        QHueso hueso = new QHueso(id, nodo.getName());
        QMatriz4 matriz = nodo.getTransformacion();
        hueso.setTransformacion(new QTransformacion(QRotacion.CUATERNION));
        hueso.getTransformacion().desdeMatrix(matriz);
        Node nodoPadre = nodo.getParent();
        if (nodoPadre != null) {
            //codigo agregado para corregir carga de los modelos MD5 y collada, se debe buscar la manera de evitar agregar los nodos que no son de los huesos
//            if (!nodoPadre.getName().equals("<MD5_Hierarchy>")
//                    && !nodoPadre.getName().equals("Scene")
//                    && !nodoPadre.getName().equals("RootNode")) {
            QHueso huesoPadre = esqueleto.getHueso(nodoPadre.getName());
            if (huesoPadre != null) {
                huesoPadre.agregarHijo(hueso);
            } else {
                if (contadorNodosSinPadre < 1) {
                    contadorNodosSinPadre++;
                    maxId++;
                    System.out.println("NO SE ENCUENTRA EL HUESO PADRE CON EL NOMBRE:{" + nodoPadre.getName() + "} para el Hijo :{" + nodo.getName() + "} " + "[" + contadorNodosSinPadre + "]. Se crea un nuevo hueso para el padre no encontrado");
                    //agrego un hueso , el padre que no se encuentra
                    QHueso nuevoHuesoPadre = crearHueso(maxId, nodoPadre, esqueleto, maxId, contadorNodosSinPadre);
                    esqueleto.agregarHueso(nuevoHuesoPadre);
                    nuevoHuesoPadre.agregarHijo(hueso);
                } else {
                    System.out.println("NO SE ENCUENTRA EL HUESO PADRE CON EL NOMBRE:{" + nodoPadre.getName() + "} para el Hijo :{" + nodo.getName() + "} " + "[" + contadorNodosSinPadre + "]. Se salta la creacion por limite alcanzado");
                }
            }
//            }
        }
        return hueso;
    }

    /**
     * Crea un esqueleto a partir del esqueleto de la lista de bones cargados
     * previamente al procesar las mallas
     *
     * @param aiMesh
     * @param nodoRaiz
     * @return
     */
    private static QEsqueleto crearEsqueleto(List<Bone> boneList, Node nodoRaiz) {
        QEsqueleto esqueleto = new QEsqueleto();
        esqueleto.setHuesos(new ArrayList<>());
        //debemos ordenar la lista para agregar primero los nodos que tienen menos padres
        //paso 1, obtener la ruta de la jerarquia
        for (Bone bone : boneList) {
            AIBone aiBone = bone.getAiBone();
            Node nodo = nodoRaiz.findByName(aiBone.mName().dataString());
            bone.setRutaJerarquia(nodo.getRuta());
        }
        //paso 2 ordeno
        Collections.sort(boneList, new Comparator<Bone>() {
            @Override
            public int compare(Bone t, Bone t1) {
                return t.getRutaJerarquia().compareTo(t1.getRutaJerarquia());
            }
        });

//        System.out.println("Lista ordenada");
//        for (Bone bone : boneList) {
//            System.out.println(bone.getRutaJerarquia());
//        }
        Integer maxId = boneList.size();
        Integer contadorNodosSinPadre = 0;// para comprobar que no se agreguen nodos vacios hasta la raiz.
        for (Bone bone : boneList) {
            Node nodo = nodoRaiz.findByName(bone.getBoneName());
            esqueleto.agregarHueso(crearHueso(bone.getBoneId(), nodo, esqueleto, maxId, contadorNodosSinPadre));
        }
        esqueleto.calcularMatricesInversas();
//
//        System.out.println("Esqueleto Creado");
//        for (QHueso hueso : esqueleto.getHuesos()) {
//            if (hueso.getPadre() == null) {
//                QEntidad.imprimirArbolEntidad(hueso, "");
//            }
//        }
        return esqueleto;
    }

    /**
     * Procesa cada nodo de la jerarquia de nodos de la escena. Por cada nodo
     * crea un esqueleto. Si en la escena hay un solo esqueleto y es compartido
     * por varios nodos de la escena, se creara un esqueleto para cada nodo
     *
     * @param aiScene
     * @param aiNodo
     * @param nodoRaiz
     * @param aiMeshes
     * @param mapaMateriales
     * @return
     */
    private static QEntidad procesarNodo(AIScene aiScene, AINode aiNodo, Node nodoRaiz, PointerBuffer aiMeshes, Map<Integer, QMaterial> mapaMateriales) {
        String nombre = aiNodo.mName().dataString();
        System.out.println("Procesando Nodo:" + nombre);
        QEntidad entidad = null;

        //***************************************************************
        //*****                 CAMARAS
        //***************************************************************
        {
            int numCamaras = aiScene.mNumCameras();
            PointerBuffer aiCamaras = aiScene.mCameras();
            for (int i = 0; i < numCamaras; i++) {
                AICamera aiCamera = AICamera.create(aiCamaras.get(i));
                if (nombre.contains(aiCamera.mName().dataString())) {
                    //crea el componente camara
                    entidad = new QCamara(nombre);
                    QCamara camara = (QCamara) entidad;
                    camara.setFOV(aiCamera.mHorizontalFOV());
                    camara.frustrumCerca = aiCamera.mClipPlaneNear();
                    camara.frustrumLejos = aiCamera.mClipPlaneFar();
                    camara.setRadioAspecto(aiCamera.mAspect());
                }
            }
        }
        // si no es una camara lo crea como una entidad Generica
        if (entidad == null) {
            entidad = new QEntidad(nombre);
        }

        //Transformacion        
        entidad.setTransformacion(new QTransformacion(QRotacion.CUATERNION));
        entidad.getTransformacion().desdeMatrix(AssimpLoader.toMatrix(aiNodo.mTransformation()));

        QEsqueleto esqueleto = null;

        //***************************************************************
        //*****           MALLAS, ESQUELETO Y ANIMACIONES
        //***************************************************************
        List<Bone> boneList = new ArrayList<>();
        int numMallas = aiNodo.mNumMeshes();
        IntBuffer aiMeshesLocal = aiNodo.mMeshes();
        if (aiMeshesLocal != null) {
            //****************** MALLAS ********************************
            for (int i = 0; i < numMallas; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(aiMeshesLocal.get(i)));
                //****************** MALLA ********************************
                QGeometria malla = procesarMalla(aiMesh, mapaMateriales, boneList);
                entidad.agregarComponente(malla);
                //****************** OBJETOS DE COLISION ********************************
                //agrega componentes fisicos para cada malla
                QFormaColision colision = new QColisionMallaConvexa(malla);
                entidad.agregarComponente(colision);
                QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.ESTATICO, 0);
                rigido.setFormaColision(colision);
                entidad.agregarComponente(rigido);
            }
        }
        //****************** ESQUELETO ********************************
        if (!boneList.isEmpty()) {
//                System.out.println("Lista de huesos encontrados " + boneList.size());
            esqueleto = crearEsqueleto(boneList, nodoRaiz);
            entidad.agregarComponente(esqueleto);

            //como ya tengo el esqueleto, recorro los vertices para apuntar los huesos de cada vertice (actualmente solo tengo los ids)
            for (QComponente componente : entidad.getComponentes()) {
                if (componente instanceof QGeometria) {
                    QGeometria malla = (QGeometria) componente;
                    for (QVertice vertice : malla.vertices) {
                        QHueso[] listaHuesos = new QHueso[vertice.getListaHuesosIds().length];
                        for (int j = 0; j < vertice.getListaHuesosIds().length; j++) {
                            listaHuesos[j] = esqueleto.getHueso(vertice.getListaHuesosIds()[j]);
                        }
                        vertice.setListaHuesos(listaHuesos);
                    }
                }
            }
        }
//        if (esqueleto != null) {
        //****************** ANIMACIONES ********************************
        QCompAlmacenAnimaciones almacen = procesarAnimaciones(aiScene, esqueleto, entidad);
        if (almacen != null) {
            entidad.agregarComponente(almacen);
            //le setea la primera animacion para q se ejecute        
            Iterator it = almacen.getAnimaciones().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                entidad.agregarComponente((QComponenteAnimacion) pair.getValue());
                break;
            }
        }
//        }

        //***************************************************************
        //*****                 LUCES
        //***************************************************************
        int numLuces = aiScene.mNumLights();
        PointerBuffer aiLuces = aiScene.mLights();
        for (int i = 0; i < numLuces; i++) {
            AILight aiLuz = AILight.create(aiLuces.get(i));
            if (nombre.contains(aiLuz.mName().dataString())) {
                //crea el componente luz
                QLuz luz = null;

                AIColor3D cDifuso = aiLuz.mColorDiffuse();

                QColor colorDifuso = new QColor(cDifuso.r() / 255.0f, cDifuso.g() / 255.0f, cDifuso.b() / 255.0f);
                AIVector3D dir;
                switch (aiLuz.mType()) {
                    case Assimp.aiLightSource_POINT:
                        luz = new QLuzPuntual();
                        break;
                    case Assimp.aiLightSource_AMBIENT:
                        break;
                    case Assimp.aiLightSource_AREA:
                        break;
                    case Assimp.aiLightSource_DIRECTIONAL:
                        dir = aiLuz.mDirection();
                        luz = new QLuzDireccional(new QVector3(dir.x(), dir.y(), dir.z()));
                        break;
                    case Assimp.aiLightSource_SPOT:
                        dir = aiLuz.mDirection();
                        luz = new QLuzSpot(new QVector3(dir.x(), dir.y(), dir.z()), aiLuz.mAngleOuterCone(), aiLuz.mAngleInnerCone());
                        break;
                }
                if (luz != null) {
                    luz.setRadio(100.0f);
                    luz.setColor(colorDifuso);
                    entidad.agregarComponente(luz);
                }
            }

        }
        //***************************************************************
        //*****                 HIJOS
        //***************************************************************
        int hijos = aiNodo.mNumChildren();
        if (hijos > 0) {
            PointerBuffer bufferHijos = aiNodo.mChildren();
            for (int i = 0; i < hijos; i++) {
                AINode nodoHijo = AINode.create(bufferHijos.get(i));
                entidad.agregarHijo(procesarNodo(aiScene, nodoHijo, nodoRaiz, aiMeshes, mapaMateriales));
            }
        }

        return entidad;
    }

    /**
     * Procesa las animaciones de la escena y devuelve un componente Almacen de
     * animaciones que contiene todas las animaciones encontradas
     *
     * @param aiScene
     * @param aiMesh
     * @param esqueleto
     * @return
     */
    private static QCompAlmacenAnimaciones procesarAnimaciones(AIScene aiScene, QEsqueleto esqueleto, QEntidad objeto) {

        QCompAlmacenAnimaciones almacen = new QCompAlmacenAnimaciones();
        boolean satisfactorio = false;

        // agrega una animacion Pose con las transformaciones originales
//        almacen.agregarAnimacion("Pose", QComponenteAnimacion.crearAnimacionPose(esqueleto));
        // Process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
            String nombreAnimacion = aiAnimation.mName().dataString();

            float duracion = (float) aiAnimation.mDuration();
            if (aiAnimation.mTicksPerSecond() > 0) {
                duracion /= aiAnimation.mTicksPerSecond();
            }
            if (nombreAnimacion == null || nombreAnimacion.isEmpty()) {
                nombreAnimacion = "Anima_Default_" + duracion;
            }
            System.out.println("            Animacion :" + nombreAnimacion);
            System.out.println("                Duracion (ticks):" + aiAnimation.mDuration());
            System.out.println("                Ticks por segundo:" + aiAnimation.mTicksPerSecond());
            System.out.println("                Duracion (s):" + duracion);

//            aiAnimation.mDuration()
            // Calculate transformation matrices for each node
            int numChanels = aiAnimation.mNumChannels();
            PointerBuffer aiChannels = aiAnimation.mChannels();

            QComponenteAnimacion animacion = new QComponenteAnimacion(duracion);
            animacion.setNombre(nombreAnimacion);
            animacion.setLoop(true);

            //las animaciones vienen separadas por canales
            //cada canal maneja por separado un hueso
            //se debe armar los frames de cada canal (hueso) y luego debemos unirlos, agregando cada canal como pares (hueso-transformacion) de una sola lista de frames
            int numeroTotalFrames = 0;
            Map<Integer, List<QAnimacionFrame>> mapa = new HashMap();
            for (int j = 0; j < numChanels; j++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
                if (esqueleto != null || objeto.getNombre().contains(aiNodeAnim.mNodeName().dataString())) {
                    List<QAnimacionFrame> framesCanal = contruirFrames(aiNodeAnim, duracion, esqueleto, objeto);
                    numeroTotalFrames = Math.max(numeroTotalFrames, framesCanal.size());
                    mapa.put(j, framesCanal);
                    satisfactorio = true;
                }
            }

            //recorro cada frame y luego cada hueso 
            for (int j = 0; j < numeroTotalFrames; j++) {
                QAnimacionFrame frame = new QAnimacionFrame(0.0f);
                float tiempo = 0.0f;
                //recorro todos los frames de cada hueso, y los uno una una lista de frames
                for (int k = 0; k < numChanels; k++) {
                    if (mapa.containsKey(k)) {
                        //si existe un frame con un par animado para este frame
                        if (mapa.get(k).size() > j) {
                            QAnimacionFrame frameHueso = mapa.get(k).get(j);
                            tiempo = Math.max(tiempo, frameHueso.getMarcaTiempo());
                            for (QParAnimacion par : frameHueso.getParesModificarAnimacion()) {
                                frame.agregarPar(par);
                            }
                        }
                    }
                }
                frame.setMarcaTiempo(tiempo);//actualiza el tiempo que fue inicializado en 0.00f
                animacion.agregarAnimacionFrame(frame);
            }
            almacen.agregarAnimacion(animacion.getNombre(), animacion);
        }
        if (satisfactorio) {
            return almacen;
        } else {
            return null;
        }
    }

    /**
     * Procesa los huesos de cada malla, extrae la informacion de los huesos y
     * carga la informacion de los pesos a la geometria
     *
     * @param malla
     * @param aiMesh
     * @param boneList
     */
    private static void procesarBones(AIMesh aiMesh, QGeometria malla, List<Bone> boneList) {
        Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            Bone bone = null;
            //si hay diferentes mallas, pueden apuntar a los mismos huesos que otras mallas
            // por lo tanto recorremos la lista comprobando si no hay ya agregado ese hueso

            for (Bone boneTmp : boneList) {
                if (boneTmp.getBoneName().equals(aiBone.mName().dataString())) {
                    bone = boneTmp;
                    break;
                }
            }

            //si no lo encontro, lo agrega
            if (bone == null) {
                int id = boneList.size();
                bone = new Bone(id, aiBone.mName().dataString(), AssimpLoader.toMatrix(aiBone.mOffsetMatrix()), aiBone);
                boneList.add(bone);
            }

            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                VertexWeight vw = new VertexWeight(bone.getBoneId(), aiWeight.mVertexId(), aiWeight.mWeight());
                List<VertexWeight> vertexWeightList = weightSet.get(vw.getVertexId());
                if (vertexWeightList == null) {
                    vertexWeightList = new ArrayList<>();
                    weightSet.put(vw.getVertexId(), vertexWeightList);
                }
                vertexWeightList.add(vw);
            }
        }

        int numVertices = aiMesh.mNumVertices();
        for (int i = 0; i < numVertices; i++) {
            List<VertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;

            //codigo para el formato QEngine
            float[] pesos = new float[size];
            int[] huesosIds = new int[size];

            for (int j = 0; j < size; j++) {
                if (j < size) {
                    VertexWeight vw = vertexWeightList.get(j);
                    pesos[j] = vw.getWeight();
                    huesosIds[j] = vw.getBoneId();
                } else {
                    pesos[j] = 0.0f;
                    huesosIds[j] = 0;
                }
            }

            //codigo para el formato QEngine
            malla.vertices[i].setListaHuesosIds(huesosIds);
            malla.vertices[i].setListaHuesosPesos(pesos);
        }
    }

    /**
     * Crea una geometria desde la malla del nodo.
     *
     * @param aiMesh
     * @param mapaMateriales
     * @param boneList
     * @return
     */
    private static QGeometria procesarMalla(AIMesh aiMesh, Map<Integer, QMaterial> mapaMateriales, List<Bone> boneList) {
        System.out.println("  Procesando malla:" + aiMesh.mName().dataString());
        QGeometria malla = new QGeometria();
        malla.nombre = aiMesh.mName().dataString();
        QMaterial material;
        int materialIdx = aiMesh.mMaterialIndex();
        if (mapaMateriales.containsKey(materialIdx)) {
            material = mapaMateriales.get(materialIdx);
        } else {
            material = new QMaterialBas();
        }
        procesarVertices(aiMesh, malla);
        procesarPoligonos(aiMesh, malla, material);
        procesarBones(aiMesh, malla, boneList);
        QUtilNormales.calcularNormales(malla);
        return malla;
    }

    /**
     * Crea una estructura de nodos simple basada en la estructura de nodos de
     * la escena. Se usa para encontrar los nodos de la escena y almacenar sus
     * transformaciones y relacionar con sus padres e hijos
     *
     * @param aiNode
     * @param parentNode
     * @return
     */
    private static Node procesarJerarquiaNodos(AINode aiNode, Node parentNode) {
        String nodeName = aiNode.mName().dataString();
        Node node = new Node(nodeName, parentNode, aiNode);

        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Node childNode = procesarJerarquiaNodos(aiChildNode, node);
            node.addChild(childNode);
        }

        return node;
    }
//
//    private static Matrix4f toMatrix_2(AIMatrix4x4 aiMatrix4x4) {
//        Matrix4f result = new Matrix4f();
//        result.m00(aiMatrix4x4.a1());
//        result.m10(aiMatrix4x4.a2());
//        result.m20(aiMatrix4x4.a3());
//        result.m30(aiMatrix4x4.a4());
//        result.m01(aiMatrix4x4.b1());
//        result.m11(aiMatrix4x4.b2());
//        result.m21(aiMatrix4x4.b3());
//        result.m31(aiMatrix4x4.b4());
//        result.m02(aiMatrix4x4.c1());
//        result.m12(aiMatrix4x4.c2());
//        result.m22(aiMatrix4x4.c3());
//        result.m32(aiMatrix4x4.c4());
//        result.m03(aiMatrix4x4.d1());
//        result.m13(aiMatrix4x4.d2());
//        result.m23(aiMatrix4x4.d3());
//        result.m33(aiMatrix4x4.d4());
//
//        return result;
//    }

    /**
     * Convierte una matriz 4x4 de ASSMIP a la matriz del engine
     *
     * @param aiMatrix4x4
     * @return
     */
    public static QMatriz4 toMatrix(AIMatrix4x4 aiMatrix4x4) {

//        return QJOMLUtil.convertirQMatriz4(toMatrix_2(aiMatrix4x4));
        QMatriz4 result = new QMatriz4(
                aiMatrix4x4.a1(), aiMatrix4x4.a2(), aiMatrix4x4.a3(), aiMatrix4x4.a4(),
                aiMatrix4x4.b1(), aiMatrix4x4.b2(), aiMatrix4x4.b3(), aiMatrix4x4.b4(),
                aiMatrix4x4.c1(), aiMatrix4x4.c2(), aiMatrix4x4.c3(), aiMatrix4x4.c4(),
                aiMatrix4x4.d1(), aiMatrix4x4.d2(), aiMatrix4x4.d3(), aiMatrix4x4.d4()
        );
//        QMatriz4 result = new QMatriz4(
//                aiMatrix4x4.a1(), aiMatrix4x4.b1(), aiMatrix4x4.c1(), aiMatrix4x4.d1(),
//                aiMatrix4x4.a2(), aiMatrix4x4.b2(), aiMatrix4x4.c2(), aiMatrix4x4.d2(),
//                aiMatrix4x4.a3(), aiMatrix4x4.b3(), aiMatrix4x4.c3(), aiMatrix4x4.d3(),
//                aiMatrix4x4.a4(), aiMatrix4x4.b4(), aiMatrix4x4.c4(), aiMatrix4x4.d4()
//        );

//        QMatriz4 result = new QMatriz4(
//                        trans.m00, trans.m01, trans.m02, trans.m03,
//                        trans.m10, trans.m11, trans.m12, trans.m13,
//                        trans.m20, trans.m21, trans.m22, trans.m23,
//                        trans.m30, trans.m31, trans.m32, trans.m33
//        
//        );
//        result.m00(aiMatrix4x4.a1());
//        result.m10(aiMatrix4x4.a2());
//        result.m20(aiMatrix4x4.a3());
//        result.m30(aiMatrix4x4.a4());
//        result.m01(aiMatrix4x4.b1());
//        result.m11(aiMatrix4x4.b2());
//        result.m21(aiMatrix4x4.b3());
//        result.m31(aiMatrix4x4.b4());
//        result.m02(aiMatrix4x4.c1());
//        result.m12(aiMatrix4x4.c2());
//        result.m22(aiMatrix4x4.c3());
//        result.m32(aiMatrix4x4.c4());
//        result.m03(aiMatrix4x4.d1());
//        result.m13(aiMatrix4x4.d2());
//        result.m23(aiMatrix4x4.d3());
//        result.m33(aiMatrix4x4.d4());
        return result;
    }

    protected static void procesarVertices(AIMesh aiMesh, QGeometria malla) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        AIVector3D aiVertex = null;
        AIVector3D textCoord = null;
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        while (aiVertices.hasRemaining()) {
            aiVertex = aiVertices.get();
            if (textCoords != null && textCoords.hasRemaining()) {
                textCoord = textCoords.get();
                malla.agregarVertice(aiVertex.x(), aiVertex.y(), aiVertex.z(), textCoord.x(), textCoord.y());
            } else {
                malla.agregarVertice(aiVertex.x(), aiVertex.y(), aiVertex.z());
            }
        }
    }

    protected static void procesarPoligonos(AIMesh aiMesh, QGeometria malla, QMaterial material) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            try {
                AIFace aiFace = aiFaces.get(i);
                IntBuffer buffer = aiFace.mIndices();
                int[] tmp = new int[aiFace.mNumIndices()];
                buffer.get(tmp);
                malla.agregarPoligono(material, tmp).setSmooth(true);
            } catch (Exception ex) {
                Logger.getLogger(AssimpLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Obtiene la textura solicitada de un material
     *
     * @param aiMaterial
     * @param tipo
     * @param texturesDir
     * @return
     */
    protected static QTextura cargaTextura(AIMaterial aiMaterial, int tipo, String texturesDir) {
        QTextura textura = null;
        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, tipo, 0, path, (IntBuffer) null, null, null, null, null, null);
        String txtPath = path.dataString();
        File file;

        if (txtPath != null && txtPath.length() > 0) {
            try {
                TextureCache textCache = TextureCache.getInstance();
                file = new File(txtPath);
                if (file.exists()) {
                    textura = textCache.getTexture(file.getAbsolutePath());
//                    textureFile = txtPath;
//                    textureFile = textureFile.replace("//", "/");
                } else {
                    file = new File(texturesDir, txtPath);
                    if (file.exists()) {
                        textura = textCache.getTexture(file.getAbsolutePath());
                    }
//                    textureFile = texturesDir + "/" + txtPath;
//                    textureFile = textureFile.replace("//", "/");

                }
//                textura = textCache.getTexture(textureFile);
            } catch (Exception ex) {
                Logger.getLogger(AssimpLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return textura;
    }

    /**
     * Crea un material desde un material de Assimp
     *
     * @param aiMaterial
     *
     * @param texturesDir
     * @return
     * @throws Exception
     */
    protected static QMaterial procesarMaterial(AIMaterial aiMaterial, String texturesDir) throws Exception {
        AIColor4D colour = AIColor4D.create();

        QTextura texturaColor = null;
        QTextura texturaNormal = null;
        QTextura texturaEmisivo = null;
        QTextura texturaTransparencia = null;
        QTextura texturaEspecular = null;
        QTextura texturaMetalica = null;
        QTextura texturaRugosidad = null;

        //textura difusa
        texturaColor = cargaTextura(aiMaterial, Assimp.aiTextureType_DIFFUSE, texturesDir);
        //textura normal
        texturaNormal = cargaTextura(aiMaterial, Assimp.aiTextureType_NORMALS, texturesDir);
        //textura emisiva/lightmap
        texturaEmisivo = cargaTextura(aiMaterial, Assimp.aiTextureType_EMISSIVE, texturesDir);
        if (texturaEmisivo == null) {
            texturaEmisivo = cargaTextura(aiMaterial, Assimp.aiTextureType_LIGHTMAP, texturesDir);
        }

        //textura transparencia
        texturaTransparencia = cargaTextura(aiMaterial, Assimp.aiTextureType_OPACITY, texturesDir);
        //textura especular
        texturaEspecular = cargaTextura(aiMaterial, Assimp.aiTextureType_SPECULAR, texturesDir);
        //textura  metalica
        texturaMetalica = cargaTextura(aiMaterial, Assimp.aiTextureType_REFLECTION, texturesDir);
        //textura de brillo (lo transofrma a rugosidad invirtiendolo)
        texturaRugosidad = cargaTextura(aiMaterial, Assimp.aiTextureType_SHININESS, texturesDir);

        int result = 0;
//        QColor ambient = QColor.WHITE;
//        result = Assimp.aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour);
//        if (result == 0) {
//            ambient = new QColor(colour.a(), colour.r(), colour.g(), colour.b());
//        }

        QColor diffuse = QColor.WHITE;
        result = Assimp.aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            diffuse = new QColor(colour.a(), colour.r(), colour.g(), colour.b());
        }

//        QColor specular = QColor.WHITE;
//        result = Assimp.aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour);
//        if (result == 0) {
//            specular = new QColor(colour.a(), colour.r(), colour.g(), colour.b());
//        }
        QMaterialBas material = new QMaterialBas();
        material.setColorBase(diffuse);
//        material.setColorEspecular(specular);

        if (texturaColor != null) {
            material.setMapaColor(new QProcesadorSimple(texturaColor));
        }
        if (texturaNormal != null) {
            material.setMapaNormal(new QProcesadorSimple(texturaNormal));
        }
        if (texturaEmisivo != null) {
            material.setMapaEmisivo(new QProcesadorSimple(texturaEmisivo));
        }
        if (texturaTransparencia != null) {
            material.setMapaTransparencia(new QProcesadorSimple(texturaTransparencia));
            material.setTransparencia(true);
        }
        if (texturaEspecular != null) {
            material.setMapaEspecular(new QProcesadorSimple(texturaEspecular));
        }
        if (texturaMetalica != null) {
            material.setMapaMetalico(new QProcesadorSimple(texturaMetalica));
        }

        if (texturaRugosidad != null) {
            //el inverso de la textura de brillo
            material.setMapaRugosidad(new QProcesadorInvierte(texturaRugosidad));
        }
        return material;
    }

}
