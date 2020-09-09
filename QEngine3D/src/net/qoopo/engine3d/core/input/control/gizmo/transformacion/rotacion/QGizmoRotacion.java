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
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QGlobal;

/**
 *
 * @author alberto
 */
public class QGizmoRotacion extends QGizmo {

    private static final List<QEntidad> ent = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/gizmos/Giz_rot.obj"));
    private static final QGeometria geoY = QUtilComponentes.getGeometria(ent.get(0));
    private static final QGeometria geoZ = QUtilComponentes.getGeometria(ent.get(1));
    private static final QGeometria geoX = QUtilComponentes.getGeometria(ent.get(2));

    public QGizmoRotacion() {
        agregarHijo(crearControladorX());
        agregarHijo(crearControladorY());
        agregarHijo(crearControladorZ());
    }

    private QGizmo crearControladorX() {
        QGizmo conX = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
//                float h = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
//                QGizmoTraslacion.this.entidad.aumentarX(h);
                QGizmoRotacion.this.entidad.aumentarRotX(getDelta(deltaX, deltaY));
                actualizarPosicionGizmo();
            }
        };

        QMaterialBas matX = new QMaterialBas("x");
        matX.setColorBase(QColor.RED);
//        matX.setFactorEmision(0.5f);
//        matX.setTransparencia(true);
//        matX.setTransAlfa(0.9f);
        QMaterialUtil.aplicarMaterial(geoX, matX);
        conX.agregarComponente(geoX);
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

        QMaterialBas matY = new QMaterialBas("y");
        matY.setColorBase(QColor.GREEN);
//        matY.setTransparencia(true);
//        matY.setTransAlfa(0.9f);
//        matY.setFactorEmision(0.5f);
        conY.agregarComponente(QMaterialUtil.aplicarMaterial(geoY, matY));

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

        QMaterialBas matZ = new QMaterialBas("z");
        matZ.setColorBase(QColor.BLUE);
//        matZ.setFactorEmision(0.5f);
//        matZ.setTransparencia(true);
//        matZ.setTransAlfa(0.9f);
        conZ.agregarComponente(QMaterialUtil.aplicarMaterial(geoZ, matZ));

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
