package net.qoopo.engine3d.componentes;

import java.io.Serializable;
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.math.Cuaternion;
import net.qoopo.engine3d.core.math.QMatriz3;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.util.TempVars;

public class QEntidad implements Serializable {

    transient private static final HashSet<Integer> usedId = new HashSet<>();

    private static synchronized int findId() {
        int result = 0;
        while (usedId.contains(result)) {
            result++;
        }
        return result;
    }

    public static void imprimirArbolEntidad(QEntidad entidad, String espacios) {
        System.out.println(espacios + entidad.nombre);
        QTransformacion tg = new QTransformacion();
        tg.desdeMatrix(entidad.getMatrizTransformacion(System.currentTimeMillis()));
        System.out.println(espacios + "TL [" + entidad.transformacion.toString() + "]");
        System.out.println(espacios + "TG [" + tg.toString() + "]");
        espacios += "    ";
        if (entidad.getHijos() != null) {
            for (QEntidad hijo : entidad.getHijos()) {
                imprimirArbolEntidad((QEntidad) hijo, espacios);
            }
        }
    }
    protected final List<QComponente> componentes = new ArrayList<>();

    //unico componente que no esta en la lista, todos necesitan este componente
    protected QTransformacion transformacion = new QTransformacion();
    protected String nombre;
    protected QEntidad padre;
    protected List<QEntidad> hijos = new ArrayList<>();
    protected boolean renderizar = true;
    protected boolean eliminar = false;
    protected long cached_time = 0;
    protected QMatriz4 cachedMatriz;

    //indica si es una entidad billboard
    /**
     * Las entidades billboard son planos que siempre apuntan a la cámara. Son
     * usados en los sistemas de particulas En el momento de la trasnformación
     * se calcula automaticamente la rotación para que siempre apunte a la
     * cámara
     */
    protected boolean billboard;

    public QEntidad() {
        this(null);
    }

    public QEntidad(String name) {
        this(name, true);
    }

    public QEntidad(String name, boolean nuevoId) {
        if (nuevoId) {
            int newId = findId();
            usedId.add(newId);
            if (name == null) {
                this.nombre = "Objeto " + newId;
            } else {
                this.nombre = name + " " + newId;
            }
        } else {
            if (name == null) {
                int newId = findId();
                usedId.add(newId);
                this.nombre = "Objeto " + newId;
            } else {
                this.nombre = name;
            }
        }
    }

    public void agregarComponente(QComponente componente) {
        if (componente == null) {
            return;
        }
        componente.entidad = this;
        componentes.add(componente);
    }

    public void eliminarComponente(QComponente componente) {
        componentes.remove(componente);
    }

    public void agregarHijo(QEntidad hijo) {
        if (!hijos.contains(hijo)) {
            hijo.setPadre(this);
            hijos.add(hijo);
        }
    }

    public boolean eliminarhijo(QEntidad hijo) {
        return hijos.remove(hijo);
    }

    @Override
    public QEntidad clone() {
        // no clono los componentes
        QEntidad nuevo = new QEntidad(nombre, false);
        nuevo.transformacion = this.transformacion.clone();
        for (QComponente comp : componentes) {
            if (comp instanceof QGeometria) {
                nuevo.agregarComponente(((QGeometria) comp).clone());
            }
        }
        return nuevo;
    }

    public void mover(float x, float y, float z) {
//        float diferenciaX = x - this.transformacion.getTraslacion().x;
//        float diferenciaY = y - this.transformacion.getTraslacion().y;
//        float diferenciaZ = z - this.transformacion.getTraslacion().z;
//        aumentarX(diferenciaX);
//        aumentarY(diferenciaY);
//        aumentarZ(diferenciaZ);
        this.transformacion.getTraslacion().set(x, y, z);
    }

    public void mover(QVector3 nuevaPosicion) {
        mover(nuevaPosicion.x, nuevaPosicion.y, nuevaPosicion.z);
    }

    public void escalar(QVector3 vector) {
        transformacion.getEscala().set(vector);
    }

    public void escalar(float x, float y, float z) {
        transformacion.getEscala().set(x, y, z);
    }

    public void escalar(float valor) {
        transformacion.getEscala().set(valor, valor, valor);
    }

    public void rotar(double angX, double angY, double angZ) {
        rotar((float) angX, (float) angY, (float) angZ);
    }

    public void rotar(float angX, float angY, float angZ) {
//        aumentarRotX(angX);
//        aumentarRotY(angY);
//        aumentarRotZ(angZ);
        transformacion.getRotacion().rotarX(angX);
        transformacion.getRotacion().rotarY(angY);
        transformacion.getRotacion().rotarZ(angZ);
    }

    public void aumentarX(float aumento) {
        this.transformacion.getTraslacion().x += aumento;
    }

    public void aumentarY(float aumento) {
        this.transformacion.getTraslacion().y += aumento;

    }

    public void aumentarZ(float aumento) {
        this.transformacion.getTraslacion().z += aumento;

    }

    public void aumentarEscalaX(float aumento) {
        this.transformacion.getEscala().x += aumento;
    }

    public void aumentarEscalaY(float aumento) {
        this.transformacion.getEscala().y += aumento;

    }

    public void aumentarEscalaZ(float aumento) {
        this.transformacion.getEscala().z += aumento;

    }

    public void aumentarRotX(float aumento) {
        this.transformacion.getRotacion().aumentarRotX(aumento);
    }

    public void aumentarRotY(float aumento) {
        this.transformacion.getRotacion().aumentarRotY(aumento);
    }

    public void aumentarRotZ(float aumento) {
        this.transformacion.getRotacion().aumentarRotZ(aumento);
    }

    public QVector3 getDireccion() {
        return transformacion.getRotacion().getCuaternion().getRotationColumn(Cuaternion.DIRECCION);
    }

    public QVector3 getIzquierda() {
        return transformacion.getRotacion().getCuaternion().getRotationColumn(Cuaternion.IZQUIERDA);
    }

    public QVector3 getArriba() {
        return transformacion.getRotacion().getCuaternion().getRotationColumn(Cuaternion.ARRIBA);
    }

    /**
     * Mueve el objeto hacia adelante si valor es positivo, o atras si es
     * negativo
     *
     * @param valor
     */
    public void moverAdelanteAtras(float valor) {
        QVector3 tmp = transformacion.getTraslacion().clone();
        tmp.add(getDireccion().multiply(-valor));
        mover(tmp);
        //la diferencia la suma a los hijos
    }

    /**
     * Mueve el objeto izquierda -derecha
     *
     * @param valor
     */
    public void moverDerechaIzquierda(float valor) {
//        transformacion.getTraslacion().add(getIzquierda().multiply(valor));
        QVector3 tmp = transformacion.getTraslacion().clone();
        tmp.add(getIzquierda().multiply(valor));
        mover(tmp);
    }

    public QEntidad getPadre() {
        return padre;
    }

    /**
     * Devuelve el padre raíz del arbol
     *
     * @return
     */
    public QEntidad getPadreRaiz() {
        if (padre != null) {
            return padre.getPadreRaiz();
        } else {
            return this;
        }
    }

    public String getRutaPadres() {
        if (padre != null) {
            return padre.getRutaPadres() + "/" + this.nombre;
        } else {
            return this.nombre;
        }
    }

    public void setPadre(QEntidad padre) {
        this.padre = padre;
    }

    public List<QEntidad> getHijos() {
        return hijos;
    }

    public void setHijos(List<QEntidad> hijos) {
        this.hijos = hijos;
    }

    /**
     * Permite rotar el objeto para que apunte a la posicion de la camara.
     *
     * @param camaraVista
     */
    public void actualizarRotacionBillboard(QMatriz4 camaraVista) {
        if (isBillboard()) {
            TempVars tv = TempVars.get();
            try {
                transformacion.getRotacion().actualizarCuaternion();

                tv.vector3f1.set(camaraVista.toTranslationVector());
                tv.vector3f1.add(this.transformacion.getTraslacion().clone().multiply(-1));
//            tv.vector3f1.normalize();
//            transformacion.getRotacion().getCuaternion().tv.vect1At(tv.vector3f1, getArriba());

//metodo tomado de jme3engine
                // coopt left for our own purposes.
                QVector3 xzp = getIzquierda();
                // The xzp vector is the projection of the tv.vector3f1 vector on the xz plane
                xzp.set(tv.vector3f1.x, 0, tv.vector3f1.z);

                tv.vector3f1.normalize();
                xzp.normalize();
                float cosp = tv.vector3f1.dotProduct(xzp);

                QMatriz3 orient = new QMatriz3();
                orient.set(0, 0, xzp.z);
                orient.set(0, 1, xzp.x * -tv.vector3f1.y);
                orient.set(0, 2, xzp.x * cosp);
                orient.set(1, 0, 0);
                orient.set(1, 1, cosp);
                orient.set(1, 2, tv.vector3f1.y);
                orient.set(2, 0, -xzp.x);
                orient.set(2, 1, xzp.z * -tv.vector3f1.y);
                orient.set(2, 2, xzp.z * cosp);
                transformacion.getRotacion().getCuaternion().fromRotationMatrix(orient);

                transformacion.getRotacion().actualizarAngulos();
            } finally {
                tv.release();
            }
        }
    }

    public QMatriz4 getMatrizTransformacion(long time) {
        try {
            if (time != cached_time || cachedMatriz == null) {
                if (transformacion == null) {
                    transformacion = new QTransformacion();
                }
                cachedMatriz = transformacion.toTransformMatriz();
                cached_time = time;
                if (padre != null) {
                    cachedMatriz = padre.getMatrizTransformacion(time).mult(cachedMatriz);
                }
            }
        } catch (Exception e) {
            System.out.println("Error en la entidad " + nombre);
            e.printStackTrace();
        }
        return cachedMatriz;
    }

    /**
     * Este metodo sera llamado por el motor de fisica despues de actualizar el
     * movimiento de la entidad Puede sobrecargar este metodo para personalizar
     * un comportamiento por ejemplo en un personaje de un video juego desee
     * comprobar colision de terreno sin la fisica pero si desea usar la fisica
     * para simular la gravedad y colision con otros objetos del entorno
     */
    public void comprobarMovimiento() {

    }

    public boolean isBillboard() {
        return billboard;
    }

    public void setBillboard(boolean billboard) {
        this.billboard = billboard;
    }

    public void destruir() {
        try {
            transformacion = null;
            if (componentes != null) {
                for (QComponente comp : componentes) {
                    comp.destruir();
                }
            }
            componentes.clear();

//            for (QEntidad hijo : hijos) {
//                hijo.destruir();
//            }
            padre = null;
            cachedMatriz = null;

        } catch (Exception e) {
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void toLista(List<QEntidad> lista) {
        if (lista == null) {
            lista = new ArrayList<>();
        }
        lista.add(this);
        if (this.getHijos() != null && !this.getHijos().isEmpty()) {
            for (QEntidad hijo : this.getHijos()) {
                hijo.toLista(lista);
            }
        }
    }

    public QTransformacion getTransformacion() {
        return transformacion;
    }

    public void setTransformacion(QTransformacion transformacion) {
        this.transformacion = transformacion;
    }

    public boolean isRenderizar() {
        return renderizar;
    }

    public void setRenderizar(boolean renderizar) {
        this.renderizar = renderizar;
    }

    public boolean isEliminar() {
        return eliminar;
    }

    public void setEliminar(boolean eliminar) {
        this.eliminar = eliminar;
    }

    public long getCached_time() {
        return cached_time;
    }

    public void setCached_time(long cached_time) {
        this.cached_time = cached_time;
    }

    public QMatriz4 getCachedMatriz() {
        return cachedMatriz;
    }

    public void setCachedMatriz(QMatriz4 cachedMatriz) {
        this.cachedMatriz = cachedMatriz;
    }

    public List<QComponente> getComponentes() {
        return componentes;
    }

}
