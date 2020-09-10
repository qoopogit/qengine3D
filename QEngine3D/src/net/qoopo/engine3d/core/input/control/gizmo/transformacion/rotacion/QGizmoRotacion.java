/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.input.control.gizmo.transformacion.rotacion;

import java.io.File;
import java.util.List;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.input.control.gizmo.QGizmo;
import net.qoopo.engine3d.core.input.control.gizmo.transformacion.traslacion.QGizmoTraslacion;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QGlobal;

/**
 *
 * @author alberto
 */
public class QGizmoRotacion extends QGizmo {

    private static final List<QEntidad> ent =  CargaWaveObject.cargarWaveObject(QGizmoTraslacion.class.getResourceAsStream("/res/gizmos/Giz_rot.obj"));
    private static final QGeometria formaY = QUtilComponentes.getGeometria(ent.get(0));
    private static final QGeometria formaZ = QUtilComponentes.getGeometria(ent.get(1));
    private static final QGeometria formaX = QUtilComponentes.getGeometria(ent.get(2));
    private static final QMaterialBas matX;
    private static final QMaterialBas matY;
    private static final QMaterialBas matZ;

  
    static {
        matX = new QMaterialBas("x");
        matX.setColorBase(QColor.RED);
        matX.setFactorEmision(0.85f);
//        matX.setTransparencia(true);
//        matX.setTransAlfa(0.9f);
        QMaterialUtil.aplicarMaterial(formaX, matX);
        matY = new QMaterialBas("y");
        matY.setColorBase(QColor.GREEN);
        matY.setFactorEmision(0.85f);
//        matY.setTransparencia(true);
//        matY.setTransAlfa(0.9f);
        QMaterialUtil.aplicarMaterial(formaY, matY);
        matZ = new QMaterialBas("z");
        matZ.setColorBase(QColor.BLUE);
        matZ.setFactorEmision(0.85f);
//        matZ.setTransparencia(true);
//        matZ.setTransAlfa(0.9f);        
        QMaterialUtil.aplicarMaterial(formaZ, matZ);
    }

    public QGizmoRotacion() {
        agregarHijo(crearControladorX());
        agregarHijo(crearControladorY());
        agregarHijo(crearControladorZ());
    }

    private QGizmo crearControladorX() {
        QGizmo conX = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoRotacion.this.entidad.aumentarRotX(getDelta(deltaX, deltaY));
                actualizarPosicionGizmo();
            }
        };

        conX.agregarComponente(formaX);
        return conX;

    }

    private QGizmo crearControladorY() {
        QGizmo conY = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoRotacion.this.entidad.aumentarRotY(getDelta(deltaX, deltaY));
                actualizarPosicionGizmo();
            }
        };
        conY.agregarComponente(formaY);
        return conY;
    }

    private QGizmo crearControladorZ() {
        QGizmo conZ = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoRotacion.this.entidad.aumentarRotZ(getDelta(deltaX, deltaY));
                actualizarPosicionGizmo();
            }
        };
        conZ.agregarComponente(formaZ);
        return conZ;
    }

    @Override
    public void actualizarPosicionGizmo() {
        try {
            if (entidad != null) {
                //actualizo posicion y rotacion
                this.transformacion.getTraslacion().set(entidad.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector());
                this.transformacion.getRotacion().setCuaternion(entidad.getMatrizTransformacion(QGlobal.tiempo).toRotationQuat());
                this.transformacion.getRotacion().actualizarAngulos();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void mouseMove(float deltaX, float deltaY) {

    }
}
