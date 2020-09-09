/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.escena;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;

/**
 * Esta entidad representa un el origen para poder dibujarlo en el escenario
 * componentes
 *
 * @author alberto
 */
public class QOrigen extends QEntidad {

//    public static final QGeometria GEOMETRIA_BONE_CUERPO = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/bones/bone.obj")).get(0));
    public QOrigen() {
        super("Origen");
        agregarGeometria();
    }

    /**
     * Esta geometria
     */
    private void agregarGeometria() {
        try {
//        agregarComponente(new QCaja(1, 1, 1));
//        agregarComponente(new QCaja(0.1f, 0.1f, 0.1f));
//        agregarComponente(new QCaja(0.5f, 0.1f, 0.1f));

//        List<QEntidad> ent = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/bones/bone.obj"));        
//        QGeometria f0 = QUtilComponentes.getGeometria(ent.get(0));
//        agregarComponente(f0);
//        agregarComponente(GEOMETRIA_BONE_CUERPO);


            QGeometria geometria = new QGeometria();
            geometria.agregarVertice(0, 0, 0); //0
            geometria.agregarVertice(1.0f, 0, 0); //1
            geometria.agregarVertice(0, 1.0f, 0); //2
            geometria.agregarVertice(0, 0, 1.0f); //3

            QMaterialBas matX = new QMaterialBas("x");
            matX.setColorBase(QColor.RED);
            matX.setFactorEmision(1.0f);
            
            QMaterialBas matY = new QMaterialBas("y");
            matY.setColorBase(QColor.GREEN);
            matY.setFactorEmision(1.0f);
            
            QMaterialBas matZ = new QMaterialBas("z");
            matZ.setColorBase(QColor.BLUE);
            matZ.setFactorEmision(1.0f);
            
            geometria.agregarLinea(matX, 0, 1);
            geometria.agregarLinea(matY, 0, 2);
            geometria.agregarLinea(matZ, 0, 3);
            agregarComponente(geometria);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
