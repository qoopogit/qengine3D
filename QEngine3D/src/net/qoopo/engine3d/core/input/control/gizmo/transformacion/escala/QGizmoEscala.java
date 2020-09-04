/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.input.control.gizmo.transformacion.escala;

import java.io.File;
import java.util.List;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
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
public class QGizmoEscala extends QGizmo {

    private static final List<QEntidad> ent = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/gizmos/Giz_esc.obj"));

    private static final QGeometria geoY = QUtilComponentes.getGeometria(ent.get(0));
    private static final QGeometria geoX = QUtilComponentes.getGeometria(ent.get(1));
    private static final QGeometria geoZ = QUtilComponentes.getGeometria(ent.get(2));

    public QGizmoEscala() {
        agregarHijo(crearControladorX());
        agregarHijo(crearControladorY());
        agregarHijo(crearControladorZ());
        agregarHijo(crearControladorTodos());
    }

    private QGizmo crearControladorTodos() {
        QGizmo todos = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoEscala.this.entidad.aumentarEscalaX(getDelta(deltaX, deltaY));
                QGizmoEscala.this.entidad.aumentarEscalaY(getDelta(deltaX, deltaY));
                QGizmoEscala.this.entidad.aumentarEscalaZ(getDelta(deltaX, deltaY));
                actualizarPosicionGizmo();
            }
        };

        QMaterialBas matTodos = new QMaterialBas("x");
        matTodos.setColorDifusa(QColor.WHITE);
//        matTodos.setTransparencia(true);
//        matTodos.setTransAlfa(0.75f);
        matTodos.setFactorEmision(.7f);
        todos.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(0.5f), matTodos));

        return todos;
    }

    private QGizmo crearControladorX() {
        QGizmo conX = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoEscala.this.entidad.aumentarEscalaX(deltaX / 10);
                actualizarPosicionGizmo();
            }
        };

        QMaterialBas matX = new QMaterialBas("x");
        matX.setColorDifusa(QColor.RED);
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
                QGizmoEscala.this.entidad.aumentarEscalaY(getDelta(deltaX, deltaY));
                actualizarPosicionGizmo();
            }
        };

        QMaterialBas matY = new QMaterialBas("y");
        matY.setColorDifusa(QColor.GREEN);
//        matY.setFactorEmision(0.5f);
//        matY.setTransparencia(true);
//        matY.setTransAlfa(0.9f);
        conY.agregarComponente(QMaterialUtil.aplicarMaterial(geoY, matY));

        return conY;
    }

    private QGizmo crearControladorZ() {
        QGizmo conZ = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoEscala.this.entidad.aumentarEscalaZ(getDelta(deltaX, deltaY));
                actualizarPosicionGizmo();
            }
        };

        QMaterialBas matZ = new QMaterialBas("z");
        matZ.setColorDifusa(QColor.BLUE);
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
