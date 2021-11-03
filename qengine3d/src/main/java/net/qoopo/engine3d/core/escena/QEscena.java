/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.escena;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.componentes.interaccion.QMouseReceptor;
import net.qoopo.engine3d.componentes.interaccion.QTecladoReceptor;
import net.qoopo.engine3d.core.input.QInputManager;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QLogger;

/**
 * Esta clase describe el universo a procesar. Contiene los objetos del
 * universo: Geometrías, Luces y Cámaras
 *
 * @author alberto
 */
public class QEscena implements Serializable {

//    public static QEscena INSTANCIA;
    private QColor colorAmbiente = QColor.BLACK;
    private final CopyOnWriteArrayList<QEntidad> listaEntidades = new CopyOnWriteArrayList<>();

    //objetos a ser usados por el motor de fisica
    public QVector3 gravedad = QVector3.gravedad.clone();
    public QUnidadMedida UM;
    private boolean bloqueado = false;

    //para pruebasm luego se agregara el control necesario y lugar correcto
    public QNeblina neblina = QNeblina.NOFOG;// sin neblina

    public QEscena() {
        UM = new QUnidadMedida();
        UM.inicializar(1, QUnidadMedida.METRO);
        gravedad = QUnidadMedida.velocidad(new QVector3(0, UM.convertirUnidades(-10, QUnidadMedida.METRO), 0));
        //actualizo como variable global
        QGlobal.gravedad = this.gravedad;
//        INSTANCIA = this;
    }

    public QEscena(QUnidadMedida unidadMedida) {
        this.UM = unidadMedida;
        gravedad = QUnidadMedida.velocidad(new QVector3(0, UM.convertirUnidades(-10, QUnidadMedida.METRO), 0));
        QGlobal.gravedad = this.gravedad;
//        INSTANCIA = this;
    }

    public void agregarEntidad(QEntidad entidad) {
        if (entidad == null) {
            return;
        }
        if (!listaEntidades.contains(entidad)) {
            listaEntidades.add(entidad);
            QLogger.info("  Agregando entidad al universo " + entidad.getNombre());
        }
        agregarHijos(entidad);
        //recorro los componentes para ver si no hay que agregar listeners al universo
        if (entidad.getComponentes() != null) {
            for (QComponente componenete : entidad.getComponentes()) {
                if (componenete instanceof QTecladoReceptor) {
                    QInputManager.agregarListenerTeclado((QTecladoReceptor) componenete);
                } else if (componenete instanceof QMouseReceptor) {
                    QInputManager.agregarListenerMouse((QMouseReceptor) componenete);
                }
            }
        }
    }

    private void agregarHijos(QEntidad objeto) {
        try {
            for (QEntidad hijo : objeto.getHijos()) {
                if (hijo != null) {
                    agregarEntidad(hijo);
                }
            }
        } catch (Exception e) {

        }
    }

    public void eliminarEntidades() {
        for (QEntidad entidad : getListaEntidades()) {
            eliminarEntidad(entidad);
        }
    }

    public void eliminarEntidad(QEntidad entidad) {
        entidad.destruir();
        listaEntidades.remove(entidad);
        //agrego los hijos al universo
        if (entidad.getHijos() != null) {
            for (QEntidad hijo : entidad.getHijos()) {
                hijo.setPadre(null);
                agregarEntidad(hijo);

            }
            entidad.getHijos().clear();
        }

        entidad.setHijos(null);
        System.gc();
    }

    public void eliminarEntidadConHijos(QEntidad entidad) {
        entidad.destruir();
        listaEntidades.remove(entidad);
        //agrego los hijos al universo
        for (QEntidad hijo : entidad.getHijos()) {
            hijo.setPadre(null);
            eliminarEntidadConHijos(hijo);
        }
        entidad.getHijos().clear();
        entidad.setHijos(null);
        System.gc();
    }

    public void eliminarEntidadSindestruir(QEntidad entidad) {
        listaEntidades.remove(entidad);
        System.gc();
    }

//    public CopyOnWriteArrayList<QEntidad> getListaEntidades() {
    public List<QEntidad> getListaEntidades() {
        esperarDesbloqueo();//espera que se desbloquee
        return listaEntidades;
    }

    public void eliminarEntidadesNoVivas() {
        bloqueado = true;
        synchronized (this) {
            List<QEntidad> tmp = new ArrayList<>();
            for (QEntidad entidad : this.listaEntidades) {
                if (entidad.isEliminar()) {
                    tmp.add(entidad);
                }
            }
            listaEntidades.removeAll(tmp);
        }
        bloqueado = false;
    }

    public void esperarDesbloqueo() {
        try {
            while (bloqueado) {
                Thread.sleep(5);//espera mientras se desbloquea
            }
        } catch (Exception e) {

        }
    }

    public QColor getColorAmbiente() {
        return colorAmbiente;
    }

    public void setColorAmbiente(QColor colorAmbiente) {
        this.colorAmbiente = colorAmbiente;
    }

}
