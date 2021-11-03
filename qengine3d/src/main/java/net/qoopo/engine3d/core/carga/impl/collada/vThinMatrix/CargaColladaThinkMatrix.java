/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.engines.animacion.QAnimacionFrame;
import net.qoopo.engine3d.engines.animacion.QParAnimacion;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.animacion.QCompAlmacenAnimaciones;
import net.qoopo.engine3d.componentes.animacion.QComponenteAnimacion;
import net.qoopo.engine3d.componentes.animacion.QEsqueleto;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;
import net.qoopo.engine3d.core.carga.CargaObjeto;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.colladaLoader.ColladaLoader;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.AnimatedModelData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.AnimationData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.JointData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.JointTransformData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.KeyFrameData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.MeshData;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.dataStructures.SkeletonData;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.util.QJOMLUtil;
import net.qoopo.engine3d.core.util.QLogger;

/**
 *
 * @author alberto
 */
public class CargaColladaThinkMatrix extends CargaObjeto {

    public CargaColladaThinkMatrix() {
    }

    @Override
    public void cargar(File archivo) {
        this.archivo = archivo;
        start();
    }

    @Override
    public void run() {
        try {
            lista.add(cargarCollada(archivo));
            //animacion
//            ColladaLoader.loadColladaAnimation(archivo);
//            lista = MD5Loader.process(md5Meshodel, md5AnimModel, QColor.WHITE);
            if (accionFinal != null) {
                accionFinal.ejecutar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static QEntidad cargarCollada(File archivo) {
        try {
            QEntidad entidad = new QEntidad();
            AnimatedModelData modelo = ColladaLoader.loadColladaModel(archivo, 10);

            QEsqueleto esqueleto = crearEsqueleto(modelo.getJointsData());
            entidad.agregarComponente(esqueleto);
            //Animacion
            AnimationData dataAnimacion = ColladaLoader.loadColladaAnimation(archivo);
            QComponenteAnimacion animacionPose = QComponenteAnimacion.crearAnimacionPose(esqueleto);
            QComponenteAnimacion animacion = crearAnimacion(dataAnimacion, esqueleto);
            QCompAlmacenAnimaciones almacen = new QCompAlmacenAnimaciones();
            almacen.agregarAnimacion("Animación", animacion);
            almacen.agregarAnimacion("Pose", animacionPose);
            entidad.agregarComponente(almacen);
            entidad.agregarComponente(animacion);
            //Geometria
            entidad.agregarComponente(cargarGeometria(modelo.getMeshData(), esqueleto));
//            System.out.println("Entidad Cargada COLLADA");
//            System.out.println("================================");
//            QEntidad.imprimirArbolEntidad(entidad, "");

            return entidad;
        } catch (Exception e) {
            QLogger.info("Erro al cargar " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static QComponenteAnimacion crearAnimacion(AnimationData dataAnimacion, QEsqueleto esqueleto) {
        QComponenteAnimacion animacion = new QComponenteAnimacion(dataAnimacion.lengthSeconds);
        animacion.setNombre("Animación");
        animacion.setLoop(true);
        for (KeyFrameData frameData : dataAnimacion.keyFrames) {
            QAnimacionFrame frame = processAnimationFrame(frameData, esqueleto);
            animacion.agregarAnimacionFrame(frame);
        }
        return animacion;
    }

    /**
     * Crea un fram de animación a partir del keyFrame Data
     *
     * @param frameData
     * @param esqueleto
     * @return
     */
    private static QAnimacionFrame processAnimationFrame(KeyFrameData frameData, QEsqueleto esqueleto) {
        QAnimacionFrame qFrame = new QAnimacionFrame(frameData.time);
        QHueso hueso;
        QTransformacion transformacion;
        for (JointTransformData joinTransFormData : frameData.jointTransforms) {
            hueso = esqueleto.getHueso(joinTransFormData.jointNameId);
            if (hueso != null) {
                QMatriz4 mat4 = QJOMLUtil.convertirQMatriz4(joinTransFormData.jointLocalTransform);
                transformacion = new QTransformacion();
                transformacion.desdeMatrix(mat4);
                qFrame.agregarPar(new QParAnimacion(hueso, transformacion));
            } else {
                System.out.println("No se encontro el hueso " + joinTransFormData.jointNameId + " para la animación");
            }
        }
        return qFrame;
    }

    /**
     * Crea un Hueso a partir de la información de joint
     *
     * @param joint
     * @return
     */
    private static QHueso crearHueso(JointData joint) {
        QHueso hueso = new QHueso(joint.index, joint.nameId);
        QMatriz4 mat4 = QJOMLUtil.convertirQMatriz4(joint.bindLocalTransform);
//        hueso.transformacion = new QTransformacion(QRotacion.CUATERNION);
        hueso.setTransformacion(new QTransformacion());
        hueso.getTransformacion().desdeMatrix(mat4);
        return hueso;
    }

    private static void procesarHuesoHijos(JointData joint, List<QHueso> lista, QHueso padre) {
        for (JointData jointItem : joint.children) {
            QHueso hueso = crearHueso(jointItem);
            padre.agregarHijo(hueso);
            lista.add(hueso);
            procesarHuesoHijos(jointItem, lista, hueso);
        }
    }

    private static QEsqueleto crearEsqueleto(SkeletonData data) {
        QEsqueleto esqueleto = new QEsqueleto();
        JointData joinData = data.headJoint;
//        int numJoints = data.jointCount;
        List<QHueso> lista = new ArrayList<>();
        QHueso hueso = crearHueso(joinData);
        lista.add(hueso);
        procesarHuesoHijos(joinData, lista, hueso);

//        System.out.println("ESQUELETO CARGADO");
//        System.out.println("=======================");
//        QEntidad.imprimirArbolEntidad(hueso, "");
        esqueleto.setHuesos(lista);
        esqueleto.calcularMatricesInversas();
        return esqueleto;
    }

    public static QGeometria cargarGeometria(MeshData data, QEsqueleto esqueleto) {
        QMaterialBas material = new QMaterialBas("default");
        QGeometria geometria = new QGeometria();

        int verticesReales = data.getVertexCount();
        int vertices = data.getVertices().length;

//        int caras = data.getIndices().length;
        int huesosTotal = data.getJointIds().length;
        int huesosXVertice = huesosTotal / verticesReales;

        //agrega los vertices, de 3 en tres por q son triangulos
        int indiceTextura = 0;
        int indiceHueso = 0;
        for (int indiceVertice = 0; indiceVertice < vertices; indiceVertice += 3) {
            QHueso[] huesos = new QHueso[huesosXVertice];
            float[] pesos = new float[huesosXVertice];
            int[] huesosIds = new int[huesosXVertice];
            QVertice vertice = geometria.agregarVertice(data.getVertices()[indiceVertice], data.getVertices()[indiceVertice + 1], data.getVertices()[indiceVertice + 2], data.getTextureCoords()[indiceTextura * 2], data.getTextureCoords()[indiceTextura * 2 + 1]);
            if (esqueleto != null) {
                for (int j = 0; j < huesosXVertice; j++) {
                    huesos[j] = esqueleto.getHueso(data.getJointIds()[indiceHueso + j]);
                    huesosIds[j] = data.getJointIds()[indiceHueso + j];
                    pesos[j] = data.getVertexWeights()[indiceHueso + j];
                }
                vertice.setListaHuesos(huesos);
                vertice.setListaHuesosIds(huesosIds);
                vertice.setListaHuesosPesos(pesos);
            }
            indiceHueso += huesosXVertice;
            indiceTextura++;
//            vertice.setListaPosicionPesos(pesosPosiciones);
        }

        //agrega los polígonos
        int i1, i2, i3;
//DE 3 EN TRES PARA HACER TRIANGULOS
        for (int i = 0; i < data.getIndices().length; i += 3) {
            i1 = data.getIndices()[i];// primer vertice
            i2 = data.getIndices()[i + 1]; //segundo vertice
            i3 = data.getIndices()[i + 2]; //tercer vertice
            try {
                geometria.agregarPoligono(material, i1, i2, i3);
            } catch (Exception ex) {
                Logger.getLogger(CargaColladaThinkMatrix.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        QUtilNormales.calcularNormales(geometria);
//        QUtilNormales.invertirNormales(objeto);
//        QMaterialUtil.suavizar(geometria, true);
        return geometria;
    }

}
