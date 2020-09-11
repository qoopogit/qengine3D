/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.animacion.esqueleto;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.math.QMatriz4;

/**
 * Esta entidad representa un hueso para la animación Esquelética. NO debe tener
 * componentes
 *
 * @author alberto
 */
public class QHueso extends QEntidad {

    public int indice = -1;

    public static final QGeometria GEOMETRIA_BONE_CUERPO = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(QHueso.class.getResourceAsStream("/res/modelos/bone/bone.obj")).get(0));
    public static final QGeometria GEOMETRIA_BONE_B1 = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(QHueso.class.getResourceAsStream("/res/modelos/bone/bone.obj")).get(1));
    public static final QGeometria GEOMETRIA_BONE_B2 = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(QHueso.class.getResourceAsStream("/res/modelos/bone/bone.obj")).get(2));

    //inversa de la transformacion Pose, es para volver el vertice a su estado normal y aplicar la transformacion de la animacion
    public QMatriz4 transformacionInversa = new QMatriz4();
//    private long cached_time_inversa = 0;
//    private QMatriz4 cachedMatrizInversa;
    protected QMatriz4 cachedMatrizConInversa = new QMatriz4();
    private long cached_time_inversa = 0;

    public QHueso() {
        agregarGeometria();
    }

    public QHueso(String name) {
        super(name, false);
        agregarGeometria();
    }

    public QHueso(int indice, String name) {
        super(name, false);
        this.indice = indice;
        agregarGeometria();
    }

    public QHueso(int indice) {
        this.indice = indice;
        agregarGeometria();
    }

    /**
     * Esta geometria es para pruebas
     */
    private void agregarGeometria() {
//        agregarComponente(new QCaja(1, 1, 1));
//        agregarComponente(new QCaja(0.1f, 0.1f, 0.1f));
//        agregarComponente(new QCaja(0.5f, 0.1f, 0.1f));

//        List<QEntidad> ent = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/bones/bone.obj"));        
//        QGeometria f0 = QUtilComponentes.getGeometria(ent.get(0));
//        agregarComponente(f0);
        agregarComponente(GEOMETRIA_BONE_CUERPO);
        agregarComponente(GEOMETRIA_BONE_B1);
        agregarComponente(GEOMETRIA_BONE_B2);

    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

//    
//    public QMatriz4 getMatrizTransformacionInversa(long time) {
//        try {
//            if (time != cached_time_inversa || cachedMatrizInversa == null) {
//                cachedMatrizInversa = this.transformacionInversa.clone();
//                cached_time_inversa = time;
//                if (super.getPadre() != null) {
//                    cachedMatrizInversa = ((QHueso)super.getPadre()).getMatrizTransformacionInversa(time).mult(cachedMatrizInversa);
//                }
//            }
//        } catch (Exception e) {
////            System.out.println("Error en la entidad " + nombre);
////            e.printStackTrace();
//        }
//        return cachedMatrizInversa;
//    }
    public QMatriz4 getTransformacionInversa() {
        return transformacionInversa;
    }

    public void setTransformacionInversa(QMatriz4 transformacionInversa) {
        this.transformacionInversa = transformacionInversa;
    }

    /**
     * Realiza el calculo de la inversa de la transformacion de este hueso
     *
     * @param parentBindTransform
     */
    public void calcularTransformacionInversa(QMatriz4 parentBindTransform) {
        try {
            QMatriz4 mat = parentBindTransform.mult(transformacion.toTransformMatriz());
            try {
                transformacionInversa = mat.invert();
            } catch (Exception e) {
                transformacionInversa = new QMatriz4();
            }
            for (QEntidad hijo : this.getHijos()) {
                if (hijo instanceof QHueso) {
                    ((QHueso) hijo).calcularTransformacionInversa(mat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Matrix4f bindTransform = Matrix4f.mul(parentBindTransform, localBindTransform, null);
//        Matrix4f.invert(bindTransform, inverseBindTransform);
//        for (Joint child : children) {
//            child.calcularTransformacionInversa(bindTransform);
//        }
    }

    public QHueso clone() {
//        QHueso nuevo = (QHueso) super.clone();

        QHueso nuevo = new QHueso(indice, nombre);
        nuevo.transformacion = this.transformacion.clone();
        nuevo.transformacionInversa = this.transformacionInversa.clone();
//        for (QComponente comp : componentes) {
//            if (comp instanceof QGeometria) {
//                nuevo.agregarComponente(((QGeometria) comp).clone());
//            }
//        }
//        if(this.getPadre()!=null)
//        {
//            nuevo.setPadre(this.getPadre().clone());
//        }
        //hijos
        if (this.getHijos() != null && !this.getHijos().isEmpty()) {
            for (QEntidad hijo : this.getHijos()) {
                nuevo.agregarHijo(((QHueso) hijo).clone());
            }
        }

        return nuevo;
    }

    /**
     * Metodo sobrecargado para agregar a la multiplicacion la matriz inversa y
     * almacenarla en el cache
     *
     * @param time
     * @return
     */
//    @Override
    public QMatriz4 getMatrizTransformacionHueso(long time) {
        try {
            if (time != cached_time_inversa || cachedMatrizConInversa == null) {
                cached_time_inversa = time;
                cachedMatrizConInversa = transformacion.toTransformMatriz();
                if (super.getPadre() != null) {
                    //toma la transformacion anidada de los padres y multiplicamos por la transformacion local
                    cachedMatrizConInversa = super.getPadre().getMatrizTransformacion(time).mult(cachedMatrizConInversa);
                }
                //multiplica por la inversa para quitar la transformacion original del vertice y aplicar la de la animacion 
                cachedMatrizConInversa = cachedMatrizConInversa.mult(transformacionInversa);
            }
        } catch (Exception e) {
            System.out.println("Error en la entidad " + nombre);
            e.printStackTrace();
        }
        return cachedMatrizConInversa;
    }
}
