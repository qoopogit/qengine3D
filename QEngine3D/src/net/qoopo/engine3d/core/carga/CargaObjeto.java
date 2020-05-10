/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.carga;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.util.Accion;

/**
 *
 * @author alberto
 */
public abstract class CargaObjeto extends Thread {

    protected JProgressBar progreso;
    protected File archivo;
    protected List<QEntidad> lista = new ArrayList<>();
    protected Accion accionFinal;

    public abstract void cargar(File archivo);

    public JProgressBar getProgreso() {
        return progreso;
    }

    public void setProgreso(JProgressBar progreso) {
        this.progreso = progreso;
    }

    public File getArchivo() {
        return archivo;
    }

    public void setArchivo(File archivo) {
        this.archivo = archivo;
    }

    public List<QEntidad> getLista() {
        return lista;
    }

    public void setLista(List<QEntidad> lista) {
        this.lista = lista;
    }

    public Accion getAccionFinal() {
        return accionFinal;
    }

    public void setAccionFinal(Accion accionFinal) {
        this.accionFinal = accionFinal;
    }

}
