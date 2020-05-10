/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.editor.util;

/**
 * Wrapper para el arbol de entidades
 * @author alberto
 */
public class QArbolWrapper {
    private String nombre;
    private Object objeto;

    public QArbolWrapper(String nombre, Object entidad) {
        this.nombre = nombre;
        this.objeto = entidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Object getObjeto() {
        return objeto;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
    
    
}
