package net.qoopo.engine3d.core.material.basico;

import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.material.QMaterial;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.math.QColor;

/**
 * Materiales básicos
 *
 * @author alberto
 */
public class QMaterialBas extends QMaterial {

    //propiedades basadas en PBR
//    private float metalico = 0.0f;
//    private float rugosidad = 0.0f;
//    private float especular = 0.0f;
    //***************** SOMBREADO *********************
    //Color de luz difusa
    private QColor colorDifusa = QColor.WHITE.clone();

    //factor de luz emitida del propio material
    private float factorEmision = 0;
    private float factorNormal = 1;

    //***************** TRANSPARENCIA *********************
    private boolean transparencia = false;
    //transparencia, 0 transparente 1 solido    
    private float transAlfa = 1.0f;
    private QColor colorTransparente = null;

    //***************** ESPECULARIDAD *********************
    //nivel de brillo, maximo 500
    private int specularExponent = 50;
    //Color de luz especular
    private QColor colorEspecular = QColor.WHITE.clone();

    //***************** ENTORNO *********************
    private int tipoMapaEntorno = 2;//1. Mapa cubico, 2. Mapa HDRI, 
    private float factorEntorno = 0.0f;//reflectividad. 1= max reflectivo menos color difuso, 0 no reflectivo mas color difuso

    //***************** REFLEXION *********************
    private boolean reflexion = false;

    //***************** REFRACCIÓN *********************
    private boolean refraccion = false;
    private float indiceRefraccion = 0.0f;//indice de refraccion, aire 1, agua 1.33, vidrio 1.52, diamante 2.42, 
    //---------------------------------------------------

    //***************** SOMBRAS *********************
    private boolean sombrasProyectar = true;
    private boolean sombrasRecibir = true;
    private boolean sombrasSoloProyectarSombra = false;// renderiza la sombra pero no renderiza el objeto, como si fuera 100% transparente

    //***************** MAPAS **********************
    //mapas
    private QProcesadorTextura mapaDifusa;//ok
    private QProcesadorTextura mapaNormal;//ok
    private QProcesadorTextura mapaEmisivo;//ok
    private QProcesadorTextura mapaEspecular;//ok
    private QProcesadorTextura mapaTransparencia;//ok
    private QProcesadorTextura mapaDesplazamiento;// 
    private QProcesadorTextura mapaEntorno;//ok. textura usada para el mapeo del entorno, reflexiones.
    private QProcesadorTextura mapaSAO;//Oclusion ambiental
    private QProcesadorTextura mapaRugosidad;//mapa rugosidad

    //esta bandera la uso para identificar si es un reflejo y debo trabajar con su proyeccion 
    //en lugar del mapeo normal
    //el calculo lo hace el render
    //solo tenemos la bandera por logistica
    private boolean difusaProyectada = false;

    public QMaterialBas() {
    }

    public QMaterialBas(QColor difusa, QColor especular, int exponenteEspecular) {
        this.colorDifusa = difusa;
        this.colorEspecular = especular;
        this.specularExponent = exponenteEspecular;
    }

    public QMaterialBas(String name) {
        this.nombre = name;
    }

    /**
     * Crea un material usando la textura dada. Para la textura se crea un proxy
     * simple sin proceso
     *
     * @param textura
     * @param specularExponent
     */
    public QMaterialBas(QTextura textura, int specularExponent) {
        this.specularExponent = specularExponent;
        this.mapaDifusa = new QProcesadorSimple(textura);
    }

    public QMaterialBas(QTextura textura) {
        this.mapaDifusa = new QProcesadorSimple(textura);
    }

    public QProcesadorTextura getMapaDifusa() {
        return mapaDifusa;
    }

    public void setMapaDifusa(QProcesadorTextura mapaDifusa) {
        this.mapaDifusa = mapaDifusa;
    }

    public QProcesadorTextura getMapaNormal() {
        return mapaNormal;
    }

    public void setMapaNormal(QProcesadorTextura mapaNormal) {
        this.mapaNormal = mapaNormal;
    }

    public boolean isDifusaProyectada() {
        return difusaProyectada;
    }

    /**
     * Identifica si es un reflejo y se debe trabajar con su proyeccion en lugar
     * dle mapeo normal, el calculo lo hace el render
     *
     * @param difusaProyectada
     */
    public void setDifusaProyectada(boolean difusaProyectada) {
        this.difusaProyectada = difusaProyectada;
    }

    public void destruir() {
        colorDifusa = null;
        colorEspecular = null;
        if (mapaDifusa != null) {
            mapaDifusa.destruir();
            mapaDifusa = null;
        }
        if (mapaNormal != null) {
            mapaNormal.destruir();
            mapaNormal = null;
        }
    }

    public QColor getColorTransparente() {
        return colorTransparente;
    }

    public void setColorTransparente(QColor colorTransparente) {
        this.colorTransparente = colorTransparente;
    }

    public float getFactorEmision() {
        return factorEmision;
    }

    public void setFactorEmision(float factorEmision) {
        this.factorEmision = factorEmision;
    }

    public QColor getColorDifusa() {
        return colorDifusa;
    }

    public void setColorDifusa(QColor colorDifusa) {
        this.colorDifusa = colorDifusa;
    }

    public QColor getColorEspecular() {
        return colorEspecular;
    }

    public void setColorEspecular(QColor colorEspecular) {
        this.colorEspecular = colorEspecular;
    }

    public boolean isTransparencia() {
        return transparencia;
    }

    public void setTransparencia(boolean transparencia) {
        this.transparencia = transparencia;
    }

    public float getTransAlfa() {
        return transAlfa;
    }

    public void setTransAlfa(float transAlfa) {
//        transparencia = transAlfa < 1.0;
        this.transAlfa = transAlfa;
    }

    public float getFactorNormal() {
        return factorNormal;
    }

    public void setFactorNormal(float factorNormal) {
        this.factorNormal = factorNormal;
    }

    public int getSpecularExponent() {
        return specularExponent;
    }

    public void setSpecularExponent(int specularExponent) {
        this.specularExponent = specularExponent;
    }

    public boolean isSombrasProyectar() {
        return sombrasProyectar;
    }

    public void setSombrasProyectar(boolean sombrasProyectar) {
        this.sombrasProyectar = sombrasProyectar;
    }

    public boolean isSombrasRecibir() {
        return sombrasRecibir;
    }

    public void setSombrasRecibir(boolean sombrasRecibir) {
        this.sombrasRecibir = sombrasRecibir;
    }

    public boolean isSombrasSoloProyectarSombra() {
        return sombrasSoloProyectarSombra;
    }

    public void setSombrasSoloProyectarSombra(boolean sombrasSoloProyectarSombra) {
        this.sombrasSoloProyectarSombra = sombrasSoloProyectarSombra;
    }

    public QProcesadorTextura getMapaEmisivo() {
        return mapaEmisivo;
    }

    public void setMapaEmisivo(QProcesadorTextura mapaEmisivo) {
        this.mapaEmisivo = mapaEmisivo;
    }

    public QProcesadorTextura getMapaEspecular() {
        return mapaEspecular;
    }

    public void setMapaEspecular(QProcesadorTextura mapaEspecular) {
        this.mapaEspecular = mapaEspecular;
    }

    public QProcesadorTextura getMapaDesplazamiento() {
        return mapaDesplazamiento;
    }

    public void setMapaDesplazamiento(QProcesadorTextura mapaDesplazamiento) {
        this.mapaDesplazamiento = mapaDesplazamiento;
    }

    public QProcesadorTextura getMapaEntorno() {
        return mapaEntorno;
    }

    public void setMapaEntorno(QProcesadorTextura mapaEntorno) {
        this.mapaEntorno = mapaEntorno;
    }

    public boolean isReflexion() {
        return reflexion;
    }

    public void setReflexion(boolean reflexion) {
        this.reflexion = reflexion;
    }

    public boolean isRefraccion() {
        return refraccion;
    }

    public void setRefraccion(boolean refraccion) {
        this.refraccion = refraccion;
    }

    public float getFactorEntorno() {
        return factorEntorno;
    }

    public void setFactorEntorno(float factorEntorno) {
        this.factorEntorno = factorEntorno;
    }

    public float getIndiceRefraccion() {
        return indiceRefraccion;
    }

    public void setIndiceRefraccion(float indiceRefraccion) {
        this.indiceRefraccion = indiceRefraccion;
    }

    public QProcesadorTextura getMapaTransparencia() {
        return mapaTransparencia;
    }

    public void setMapaTransparencia(QProcesadorTextura mapaTransparencia) {
        this.mapaTransparencia = mapaTransparencia;
    }

    public int getTipoMapaEntorno() {
        return tipoMapaEntorno;
    }

    public void setTipoMapaEntorno(int tipoMapaEntorno) {
        this.tipoMapaEntorno = tipoMapaEntorno;
    }

    public QProcesadorTextura getMapaSAO() {
        return mapaSAO;
    }

    public void setMapaSAO(QProcesadorTextura mapaSAO) {
        this.mapaSAO = mapaSAO;
    }

//    public float getMetalico() {
//        return metalico;
//    }
//
//    public void setMetalico(float metalico) {
//        this.metalico = metalico;
//    }
//
//    public float getRugosidad() {
//        return rugosidad;
//    }
//
//    public void setRugosidad(float rugosidad) {
//        this.rugosidad = rugosidad;
//    }
//
//    public float getEspecular() {
//        return especular;
//    }
//
//    public void setEspecular(float especular) {
//        this.especular = especular;
//    }
    public QProcesadorTextura getMapaRugosidad() {
        return mapaRugosidad;
    }

    public void setMapaRugosidad(QProcesadorTextura mapaRugosidad) {
        this.mapaRugosidad = mapaRugosidad;
    }

}
