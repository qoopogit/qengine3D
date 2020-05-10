/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes;

import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.componentes.animacion.QCompAlmacenAnimaciones;
import net.qoopo.engine3d.componentes.animacion.QComponenteAnimacion;
import net.qoopo.engine3d.componentes.animacion.QEsqueleto;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.fisica.colisiones.listeners.QListenerColision;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QVehiculo;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.interaccion.QTecladoReceptor;
import net.qoopo.engine3d.componentes.modificadores.procesadores.agua.QProcesadorAguaSimple;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.componentes.terreno.QTerreno;

/**
 *
 * @author alberto
 */
public class QUtilComponentes {

//     public void  eliminarComponenteAnimacion(QEntidad entidad,Class<T> controlType) {
//         List<QComponente> tmp = new ArrayList<>();
//        for (QComponente componente : entidad.componentes) {
//            if (controlType.isAssignableFrom(componente.getClass())) {
//                tmp.add(componente);
//            }
//        }
//        entidad.componentes.removeAll(tmp);
//    }
    public static void eliminarComponenteAnimacion(QEntidad entidad, String clase) {
        List<QComponente> tmp = new ArrayList<>();
        for (QComponente componente : entidad.componentes) {
            if (componente.getClass().getName().equals(clase)) {
                tmp.add(componente);
            }
        }
        entidad.componentes.removeAll(tmp);
    }

    public static void eliminarComponenteTeclado(QEntidad entidad) {
        List<QComponente> tmp = new ArrayList<>();
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QTecladoReceptor) {
                tmp.add(componente);
            }
        }
        entidad.componentes.removeAll(tmp);
    }

    public static void eliminarComponenteGeometria(QEntidad entidad) {
        List<QComponente> tmp = new ArrayList<>();
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QGeometria) {
                tmp.add(componente);
            }
        }
        entidad.componentes.removeAll(tmp);
    }

    public static void eliminarComponenteAnimacion(QEntidad entidad) {
        List<QComponente> tmp = new ArrayList<>();
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QComponenteAnimacion) {
                tmp.add(componente);
            }
        }
        entidad.componentes.removeAll(tmp);
    }

    public static QCompAlmacenAnimaciones getAlmacenAnimaciones(QEntidad entidad) {
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QCompAlmacenAnimaciones) {
                return (QCompAlmacenAnimaciones) componente;
            }
        }
        return null;
    }

    public static QEsqueleto getEsqueleto(QEntidad entidad) {
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QEsqueleto) {
                return (QEsqueleto) componente;
            }
        }
        return null;
    }

    public static QMapaCubo getMapaCubo(QEntidad entidad) {
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QMapaCubo) {
                return (QMapaCubo) componente;
            }
        }
        return null;
    }

    public static void eliminarComponenteAguaSimple(QEntidad entidad) {
        List<QComponente> tmp = new ArrayList<>();
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QProcesadorAguaSimple) {
                tmp.add(componente);
            }
        }
        entidad.componentes.removeAll(tmp);
    }

    public static void eliminarComponenteMapaCubo(QEntidad entidad) {
        List<QComponente> tmp = new ArrayList<>();
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QMapaCubo) {
                tmp.add(componente);
            }
        }
        entidad.componentes.removeAll(tmp);
    }

    public static QGeometria getGeometria(QEntidad entidad) {
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QGeometria) {
                return (QGeometria) componente;
            }
        }
        return null;
    }

    public static QTerreno getTerreno(QEntidad entidad) {
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QTerreno) {
                return (QTerreno) componente;
            }
        }
        return null;
    }

    public static QFormaColision getColision(QEntidad entidad) {
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QFormaColision) {
                return (QFormaColision) componente;
            }
        }
        return null;
    }

    public static QObjetoRigido getFisicoRigido(QEntidad entidad) {
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QObjetoRigido) {
                return (QObjetoRigido) componente;
            }
        }
        return null;
    }

    public static QVehiculo getVehiculo(QEntidad entidad) {
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QVehiculo) {
                return (QVehiculo) componente;
            }
        }
        return null;
    }

    public static List<QListenerColision> getColisionListeners(QEntidad entidad) {
        List<QListenerColision> tmp = new ArrayList<>();
        for (QComponente componente : entidad.componentes) {
            if (componente instanceof QListenerColision) {
                tmp.add((QListenerColision) componente);
            }
        }
        return tmp;
    }

    public static QComponente getComponente(QEntidad entidad, String clase) {
        for (QComponente componente : entidad.componentes) {
            if (componente.getClass().getName().equals(clase)) {
                return componente;
            }
        }
        return null;
    }
}
