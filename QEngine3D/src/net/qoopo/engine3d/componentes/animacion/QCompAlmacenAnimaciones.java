/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.animacion;

import java.util.HashMap;
import java.util.Map;
import net.qoopo.engine3d.componentes.QComponente;

/**
 * Esta clase es un alamacen de animaciones que tendra una entidad Se peude
 * tener varias animaciones por ejemplo una animacion con el nombre "caminar"
 *
 * @author alberto
 */
public class QCompAlmacenAnimaciones extends QComponente {

    private final Map<String, QComponenteAnimacion> animaciones = new HashMap<>();

    public void agregarAnimacion(String identificador, QComponenteAnimacion animacion) {
        animaciones.put(identificador, animacion);
    }

    public void eliminarAnimacion(String identificador) {
        animaciones.remove(identificador);
    }

    public QComponenteAnimacion getAnimacion(String identificador) {
        return animaciones.get(identificador);
    }

    public Map<String, QComponenteAnimacion> getAnimaciones() {
        return animaciones;
    }

    @Override
    public void destruir() {
        animaciones.clear();
    }

}
