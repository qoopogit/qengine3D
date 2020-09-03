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
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;
import net.qoopo.engine3d.core.material.QMaterial;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.Cuaternion;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.math.QRotacion;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.engines.animacion.QAnimacionFrame;
import net.qoopo.engine3d.engines.animacion.QParAnimacion;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
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
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
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
        return AssimpLoader.cargarAssimpItems(resourcePath, texturesDir, Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_LimitBoneWeights);
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
        AIScene aiScene = aiImportFile(rutaArchivo, flags);

        System.out.println("Cargando Recurso con ASSIMP");
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
        PointerBuffer aiCameras = aiScene.mCameras();
        PointerBuffer aiLights = aiScene.mLights();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        PointerBuffer aiTextures = aiScene.mTextures();

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
    private static List<QAnimacionFrame> contruirFrames(AINodeAnim aiNodeAnim, float duracionTotalAnimacion, QEsqueleto esqueleto) {
        int numFrames = aiNodeAnim.mNumPositionKeys();
        //Cada frame deberia tener configurado el tiempo, sin embargo al no encontrarlo asignamos equitativamente el tiempo para cada frame
        float duracion = (float) duracionTotalAnimacion / numFrames;
        List<QAnimacionFrame> frameList = new ArrayList<>();

        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        for (int i = 0; i < numFrames; i++) {
            QAnimacionFrame frame = new QAnimacionFrame(duracion * i);
            QHueso hueso = esqueleto.getHueso(aiNodeAnim.mNodeName().dataString());
            if (hueso != null) {

                QTransformacion transformacion = new QTransformacion(QRotacion.CUATERNION);

                AIVectorKey aiVecKey = positionKeys.get(i);
                AIVector3D vec = aiVecKey.mValue();
                AIQuatKey quatKey = rotationKeys.get(i);
                AIQuaternion aiQuat = quatKey.mValue();

                transformacion.getTraslacion().setXYZ(vec.x(), vec.y(), vec.z());

                Cuaternion quat = new Cuaternion(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
                transformacion.getRotacion().setCuaternion(quat);

                if (i < aiNodeAnim.mNumScalingKeys()) {
                    aiVecKey = scalingKeys.get(i);
                    vec = aiVecKey.mValue();
//                    matrizTransformacion.setScale(vec.x(), vec.y(), vec.z());
                    transformacion.getEscala().setXYZ(vec.x(), vec.y(), vec.z());
                }

//                transformacion = new QTransformacion();
//                transformacion.desdeMatrix(matrizTransformacion);
                frame.agregarPar(new QParAnimacion(hueso, transformacion));
                frameList.add(frame);
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
//        System.out.println("Procesando Nodo:" + aiNodo.mName().dataString());

        QEntidad entidad = new QEntidad(aiNodo.mName().dataString());

        //Transformacion        
        entidad.setTransformacion(new QTransformacion(QRotacion.CUATERNION));
        entidad.getTransformacion().desdeMatrix(AssimpLoader.toMatrix(aiNodo.mTransformation()));

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

            //****************** ESQUELETO ********************************
            if (!boneList.isEmpty()) {
//                System.out.println("Lista de huesos encontrados " + boneList.size());
                QEsqueleto esqueleto = crearEsqueleto(boneList, nodoRaiz);
                entidad.agregarComponente(esqueleto);

                //como ya tengo el esqueleto, recorro los vertices para apuntar los huesos de cada vertice (actualmente solo tengo los ids)
                for (QComponente componente : entidad.getComponentes()) {
                    if (componente instanceof QGeometria) {
                        QGeometria malla = (QGeometria) componente;
                        for (QVertice vertice : malla.listaVertices) {
                            QHueso[] listaHuesos = new QHueso[vertice.getListaHuesosIds().length];
                            for (int j = 0; j < vertice.getListaHuesosIds().length; j++) {
                                listaHuesos[j] = esqueleto.getHueso(vertice.getListaHuesosIds()[j]);
                            }
                            vertice.setListaHuesos(listaHuesos);
                        }
                    }
                }

                //****************** ANIMACIONES ********************************
                QCompAlmacenAnimaciones almacen = procesarAnimaciones(aiScene, esqueleto);
                entidad.agregarComponente(almacen);
                //le setea la primera animacion para q se ejecute        
                Iterator it = almacen.getAnimaciones().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    entidad.agregarComponente((QComponenteAnimacion) pair.getValue());
                    break;
                }

//            entidades.add(entidad);
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
    private static QCompAlmacenAnimaciones procesarAnimaciones(AIScene aiScene, QEsqueleto esqueleto) {

        QCompAlmacenAnimaciones almacen = new QCompAlmacenAnimaciones();

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
//            System.out.println("            Animacion :" + nombreAnimacion);
//            System.out.println("                Duracion (ticks):" + aiAnimation.mDuration());
//            System.out.println("                Ticks por segundo:" + aiAnimation.mTicksPerSecond());
//            System.out.println("                Duracion (s):" + duracion);

//            aiAnimation.mDuration()
            // Calculate transformation matrices for each node
            int numChanels = aiAnimation.mNumChannels();
            PointerBuffer aiChannels = aiAnimation.mChannels();

            QComponenteAnimacion animacion = new QComponenteAnimacion(duracion);

            animacion.setNombre(nombreAnimacion);
            animacion.setLoop(true);

            //las animaciones vienen separadas por canales
            //cada canal maneja por seaprado un hueso
            //se debe armar los frames de cada canal (hueso) y luego debemos unirlos, agregando cada canal como pares (hueso-transformacion) de una sola lista de frames
            int numeroTotalFrames = 0;
            Map<Integer, List<QAnimacionFrame>> mapa = new HashMap();
            for (int j = 0; j < numChanels; j++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
                List<QAnimacionFrame> framesCanal = contruirFrames(aiNodeAnim, duracion, esqueleto);
                numeroTotalFrames = Math.max(numeroTotalFrames, framesCanal.size());
                mapa.put(j, framesCanal);
            }

//            System.out.println("Total de frames=" + numeroTotalFrames);
            //recorro cada frame y luego cada hueso para
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
//                System.out.println("Frame [" + (j + 1) + "] Tiempo:" + tiempo);
//                System.out.println(" Pares:" + frame.getParesModificarAnimacion().size());
                frame.setMarcaTiempo(tiempo);//actualiza el tiempo que fue inicializado en 0.00f
                animacion.agregarAnimacionFrame(frame);
            }
            almacen.agregarAnimacion(animacion.getNombre(), animacion);
        }
        return almacen;
    }

    /**
     * Procesa los huesos de cada malla, extrae la informacion de los huesos y
     * carga la informacion de los pesos a la geometria
     *
     * @param malla
     * @param aiMesh
     * @param boneList
     */
    private static void processBones(QGeometria malla, AIMesh aiMesh, List<Bone> boneList) {
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

            //si no lo necontro, lo agrega
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

            //codigo original
//            for (int j = 0; j < Mesh.MAX_WEIGHTS; j++) {
            for (int j = 0; j < size; j++) {
                if (j < size) {
                    VertexWeight vw = vertexWeightList.get(j);
                    pesos[j] = vw.getWeight();
                    huesosIds[j] = vw.getBoneId();
//                    weights.add(vw.getWeight());
//                    boneIds.add(vw.getBoneId());
                } else {
                    pesos[j] = 0.0f;
                    huesosIds[j] = 0;
//                    weights.add(0.0f);
//                    boneIds.add(0);
                }
            }

            //codigo para el formato QEngine
//            malla.listaVertices[i].setListaHuesos(huesos);
            malla.listaVertices[i].setListaHuesosIds(huesosIds);
            malla.listaVertices[i].setListaHuesosPesos(pesos);
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

//        List<Integer> boneIds = new ArrayList<>();
//        List<Float> weights = new ArrayList<>();
        QGeometria malla = new QGeometria();
        malla.nombre = aiMesh.mName().dataString();

        QMaterial material;
        int materialIdx = aiMesh.mMaterialIndex();

//        System.out.println("Indice del material :" + materialIdx);
        if (mapaMateriales.containsKey(materialIdx)) {
            material = mapaMateriales.get(materialIdx);
        } else {
            material = new QMaterialBas();
        }

//        if (materialIdx >= 0 && materialIdx < materials.size()) {
//            material = materials.get(materialIdx);
//        } else {
//            material = new QMaterialBas();
//        }
        procesarVertices(aiMesh, malla);
        procesarPoligonos(aiMesh, malla, material);

//        processVertices(aiMesh, vertices);
//        processNormals(aiMesh, normals);
//        processTextCoords(aiMesh, textures);
//        processIndices(aiMesh, indices);
//        processBones(malla, aiMesh, boneList, boneIds, weights);
        processBones(malla, aiMesh, boneList);

//        Mesh mesh = new Mesh(Utils.listToArray(vertices), Utils.listToArray(textures),
//                Utils.listToArray(normals), Utils.listIntToArray(indices),
//                Utils.listIntToArray(boneIds), Utils.listToArray(weights));
//        Material material;
//        int materialIdx = aiMesh.mMaterialIndex();
//        if (materialIdx >= 0 && materialIdx < materials.size()) {
//            material = materials.get(materialIdx);
//        } else {
//            material = new Material();
//        }
//        mesh.setMaterial(material);
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
        while (aiVertices.remaining() > 0) {
            aiVertex = aiVertices.get();
            if (textCoords != null && textCoords.hasRemaining()) {
                textCoord = textCoords.get();
                malla.agregarVertice(aiVertex.x(), aiVertex.y(), aiVertex.z(), textCoord.x(), textCoord.y());
            } else {
                malla.agregarVertice(aiVertex.x(), aiVertex.y(), aiVertex.z());
            }
//            malla.agregarVertice(aiVertex.x(), aiVertex.y(), aiVertex.z(), textCoord.x(), 1.0f-textCoord.y());
//            vertices.add(aiVertex.x());
//            vertices.add(aiVertex.y());
//            vertices.add(aiVertex.z());

        }
    }

    protected static void procesarPoligonos(AIMesh aiMesh, QGeometria malla, QMaterial material) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            try {
                AIFace aiFace = aiFaces.get(i);
                IntBuffer buffer = aiFace.mIndices();
                List<Integer> tmp = new ArrayList<>();
                while (buffer.remaining() > 0) {
                    tmp.add(buffer.get());
                }
                int[] tmp2 = new int[tmp.size()];
                for (int ii = 0; ii < tmp.size(); ii++) {
                    tmp2[ii] = tmp.get(ii);
                }
                malla.agregarPoligono(material, tmp2).smooth = true;
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

        QTextura texturaDifuso = null;
        QTextura texturaNormal = null;
        QTextura texturaEmisivo = null;
        QTextura texturaTransparencia = null;
        QTextura texturaEspecular = null;

        //textura difusa
        texturaDifuso = cargaTextura(aiMaterial, Assimp.aiTextureType_DIFFUSE, texturesDir);
        //textura normal
        texturaNormal = cargaTextura(aiMaterial, Assimp.aiTextureType_NORMALS, texturesDir);
        //textura emisiva
        texturaEmisivo = cargaTextura(aiMaterial, Assimp.aiTextureType_EMISSIVE, texturesDir);
        //textura emisiva
        texturaTransparencia = cargaTextura(aiMaterial, Assimp.aiTextureType_OPACITY, texturesDir);
        //textura especular
        texturaEspecular = cargaTextura(aiMaterial, Assimp.aiTextureType_SPECULAR, texturesDir);

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

//        QMaterial material = new Material(ambient, diffuse, specular, 1.0f);
        QMaterialBas material = new QMaterialBas();
        material.setColorDifusa(diffuse);
//        material.setColorEspecular(specular);
//        material.setDifusaProyectada(false);
        if (texturaDifuso != null) {
            material.setMapaDifusa(new QProcesadorSimple(texturaDifuso));
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
        return material;
    }

}
