/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.input.control.gizmo.transformacion.traslacion;

import java.io.File;
import java.util.List;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlanoBilateral;
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
public class QGizmoTraslacion extends QGizmo {

    private static final List<QEntidad> ent = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/gizmos/Giz_mov.obj"));
    private static final QGeometria formaY = QUtilComponentes.getGeometria(ent.get(0));
    private static final QGeometria formaX = QUtilComponentes.getGeometria(ent.get(1));
    private static final QGeometria formaZ = QUtilComponentes.getGeometria(ent.get(2));
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

    public QGizmoTraslacion() {
        agregarHijo(crearControladorX());
        agregarHijo(crearControladorY());
        agregarHijo(crearControladorZ());
        //planos
        agregarHijo(crearControladorPlanoXZ());
        agregarHijo(crearControladorPlanoXY());
        agregarHijo(crearControladorPlanoZY());
    }

    private QGizmo crearControladorX() {
        QGizmo conX = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoTraslacion.this.entidad.aumentarX(getDelta(deltaX, deltaY));
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
                QGizmoTraslacion.this.entidad.aumentarY(getDelta(deltaX, deltaY));
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
                QGizmoTraslacion.this.entidad.aumentarZ(getDelta(deltaX, deltaY));
                actualizarPosicionGizmo();
            }
        };
        conZ.agregarComponente(formaZ);
        return conZ;
    }

    private QGizmo crearControladorPlanoXZ() {
        QGizmo conXZ = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoTraslacion.this.entidad.aumentarX(getDelta(deltaX));
                QGizmoTraslacion.this.entidad.aumentarZ(getDelta(deltaY));
                actualizarPosicionGizmo();
            }
        };
        conXZ.mover(0.85f, 0, 0.85f);

        conXZ.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlanoBilateral(0.85f, 0.85f), matY));

        return conXZ;
    }

    private QGizmo crearControladorPlanoXY() {
        QGizmo conXY = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoTraslacion.this.entidad.aumentarX(getDelta(deltaX));
                QGizmoTraslacion.this.entidad.aumentarY(getDelta(deltaY));
                actualizarPosicionGizmo();
            }
        };
        conXY.mover(0.85f, 0.85f, 0);
        conXY.rotar(Math.toRadians(90), 0, 0);
        conXY.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlanoBilateral(0.85f, 0.85f), matZ));
        return conXY;
    }

    private QGizmo crearControladorPlanoZY() {
        QGizmo conZY = new QGizmo() {
            @Override
            public void mouseMove(float deltaX, float deltaY) {
                QGizmoTraslacion.this.entidad.aumentarZ(getDelta(deltaX));
                QGizmoTraslacion.this.entidad.aumentarY(getDelta(deltaY));
                actualizarPosicionGizmo();
            }
        };
        conZY.mover(0, 0.85f, 0.85f);
        conZY.rotar(0, 0, Math.toRadians(90));
        conZY.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlanoBilateral(0.85f, 0.85f), matX));
        return conZY;
    }

    @Override
    public void mouseMove(float deltaX, float deltaY) {

    }

    @Override
    public void actualizarPosicionGizmo() {
        try {
            if (entidad != null) {
                this.transformacion.getTraslacion().set(entidad.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector());
            }
        } catch (Exception e) {
        }
    }

}
