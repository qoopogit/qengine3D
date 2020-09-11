package net.qoopo.engine3d.engines.render.java3d;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.Light;
import javax.media.j3d.Node;
import javax.media.j3d.OrderedGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import net.qoopo.engine3d.QTime;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.modificadores.particulas.QEmisorParticulas;
import net.qoopo.engine3d.componentes.modificadores.particulas.QParticula;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QMatriz3;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.util.QLogger;
import net.qoopo.engine3d.core.util.QVectMathUtil;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.java3d.util.Java3DUtil;
import net.qoopo.engine3d.engines.render.superficie.Superficie;

public class QRenderJava3D extends QMotorRender {

    //el escena de java3D
    private SimpleUniverse universoJava3D;
    private Canvas3D canvas3D;
//    private BranchGroup escena;
    private BranchGroup escenaJava3D;
    private Map<String, List<Node>> agregados = new HashMap<>();

    private Color3f ambientColor;

    private Group raiz;//lo declaro aqui para poder enviarlo a las luces para y darle como el ambito este grupo global

    public QRenderJava3D(QEscena universo, Superficie superficie, int ancho, int alto) {
        super(universo, superficie, ancho, alto);
//        preIniciar();
    }

    public QRenderJava3D(QEscena universo, String nombre, Superficie superficie, int ancho, int alto) {
        super(universo, nombre, superficie, ancho, alto);
//        preIniciar();
    }

    @Override
    public void iniciar() {
        canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        try {
            //superficie que component es QPanel
            superficie.getComponente().getParent().add(canvas3D);
            superficie.getComponente().getParent().remove(superficie.getComponente());

        } catch (Exception e) {
            e.printStackTrace();
        }
//        superficie.setComponente(canvas3D);
        System.out.println("se va a volver a agregar el listener");
        agregarListeners(canvas3D);//vuelve a cargar los listener para el renderer
        universoJava3D = new SimpleUniverse(canvas3D);
        crearEscena();
        universoJava3D.getViewingPlatform().setNominalViewingTransform();
        universoJava3D.addBranchGraph(escenaJava3D);
        setearPerspectiva();
//        setearOrtho();
//        escena.getViewer().getView().setBackClipDistance(20);
        universoJava3D.getViewer().getView().setBackClipDistance(1000);
    }

    @Override
    public void detener() {
    }

    @Override
    public long update() {

//        actualizarEscena();
        moverCamara(universoJava3D);
//        modificadoresGeometria();
        ambientColor.set(escena.getColorAmbiente().getColor());

        actualizarObjetos();
//        modificadoresTexturas();
        try {
//            Thread.sleep(17);//60 fps en la actualizacion, en este caso de java3d no influye en los fps del renderer
            Thread.sleep(50);//20 fps en la actualizacion, en este caso de java3d no influye en los fps del renderer
//            Thread.sleep(200);//5 fps en la actualizacion, en este caso de java3d no influye en los fps del renderer
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        mostrarEstadisticas(canvas3D.getGraphics());
        //actualiza la textura de salida
        try {
            if (textura != null) {
//                BufferedImage tempImage = new BufferedImage(frameBuffer.getAncho(), frameBuffer.getAlto(), BufferedImage.TYPE_3BYTE_BGR);
//                canvas3D.paint(tempImage.getGraphics());
//                tempImage.getGraphics().dispose();
//                textura.cargarTextura(tempImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        poligonosDibujados = poligonosDibujadosTemp;
        QLogger.info("MR-J3D-->" + DF.format(getFPS()) + " FPS");
        tiempoPrevio = System.currentTimeMillis();

        return tiempoPrevio;
    }

    /**
     * actualiza las luces de acuerdo a la coordenada de la camara
     */
    private void setearPerspectiva() {
        try {
            TransformGroup perspectiva = universoJava3D.getViewingPlatform().getViewPlatformTransform();
            Transform3D transformPerspectiva = new Transform3D();
            perspectiva.getTransform(transformPerspectiva);
//            float near, float far, float left, float right, float top, float bottom, boolean parallel
//            transformPerspectiva.frustum(1.f, Float.POSITIVE_INFINITY, -0.5f, 0.5f, 0.5f, -0.5f);
            transformPerspectiva.frustum(-0.5f, -0.5f, 1.f, 0.5f, 0.5f, Float.POSITIVE_INFINITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setearOrtho() {
        try {
            TransformGroup perspectiva = universoJava3D.getViewingPlatform().getViewPlatformTransform();
            Transform3D transformPerspectiva = new Transform3D();
            perspectiva.getTransform(transformPerspectiva);
//            float near, float far, float left, float right, float top, float bottom, boolean parallel
//            transformPerspectiva.frustum(1.f, Float.POSITIVE_INFINITY, -0.5f, 0.5f, 0.5f, -0.5f);
            transformPerspectiva.ortho(-0.5f, -0.5f, 1.f, 0.5f, 0.5f, Float.POSITIVE_INFINITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moverCamara(SimpleUniverse universo) {

        try {
            TransformGroup perspectiva = universo.getViewingPlatform().getViewPlatformTransform();
            Transform3D transformPerspectiva = new Transform3D();

            QVector3 camPosicion = camara.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector();

            QVector3 objetivo = camPosicion.clone().add(camara.getDireccion().clone().multiply(-1));
            transformPerspectiva.lookAt(
                    new Point3d(camPosicion.x, camPosicion.y, camPosicion.z),
                    new Point3d(objetivo.x, objetivo.y, objetivo.z),
                    new Vector3d(camara.getArriba().x, camara.getArriba().y, camara.getArriba().z)
            );
            transformPerspectiva.invert();
            perspectiva.setTransform(transformPerspectiva);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//     private BranchGroup dibujaBola() {
//        BranchGroup bola = new BranchGroup();
//
//        int paratextura = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;
//        Sphere forma = new Sphere(0.7f, paratextura, crearApariencia());
//
//        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
//        Vector3f light1Direction = new Vector3f(0.0f, 0.0f, -1f);
//
//        DirectionalLight lightA = new DirectionalLight(lightColor, light1Direction);
//        lightA.setInfluencingBounds(new BoundingSphere());
//
//        AmbientLight ambientLightNode = new AmbientLight(lightColor);
//        ambientLightNode.setInfluencingBounds(limites);
//
//        TransformGroup tg = new TransformGroup();
//        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//
//        Transform3D t3d = new Transform3D();
//        Vector3f vector = new Vector3f(0.0f, 2.0f, -2.0f);
//        t3d.setTranslation(vector);
//        tg.setTransform(t3d);
//        tg.addChild(forma);
//        bola.addChild(tg);
//        bola.addChild(lightA);
//        bola.addChild(ambientLightNode);
//
//        //rotar
//        MouseRotate myMouseRotate = new MouseRotate();
//        myMouseRotate.setTransformGroup(tg);
//        myMouseRotate.setSchedulingBounds(new BoundingSphere());
//        bola.addChild(myMouseRotate);
//        //zoom
//        MouseZoom myMouseZoom = new MouseZoom();
//        myMouseZoom.setTransformGroup(tg);
//        myMouseZoom.setSchedulingBounds(new BoundingSphere());
//        bola.addChild(myMouseZoom);
//        return bola;
//    }
    /**
     * Convierte la entidad y sus componentes de QMotorEngina a los objetos
     * relativos en Java3D (geometrias, luces)
     *
     * @param entidad
     * @param raiz
     */
//    private void agregarEntidad(QEntidad entidad, OrderedGroup raiz) {
    private void agregarEntidad(QEntidad entidad, Group raiz) {
        Node tmp = null;
        if (entidad == null) {
            return;
        }
        List<Node> lista = new ArrayList<>();

        if (agregados.get(entidad.getRutaPadres()) == null) {
            try {
                //bandera para descartar otro tipo de componentes que no se agregan
                boolean aplica = false;
                int c = 0;
                for (QComponente componente : entidad.getComponentes()) {
                    tmp = null;
                    aplica = false;
                    if (componente instanceof QGeometria || componente instanceof QLuz) {
                        aplica = true;
                        try {
                            tmp = Java3DUtil.crearNodo(componente);
                            if (componente instanceof QLuz) {
                                //le doy como alcance el grupo raiz
                                ((Light) ((TransformGroup) tmp).getChild(0)).addScope(raiz);
                            }
                        } catch (Exception e) {
                            QLogger.info("QRenderJava3D - Error al convertir a nodo " + e.getMessage());
                            e.printStackTrace();
                            tmp = null;
                        }
                    }

                    //se agrega el nodo
                    if (aplica) {
                        c++;
                        if (tmp != null) {
                            lista.add(tmp);
                            if (escenaJava3D.indexOfChild(tmp) >= 0) {
                                QLogger.info("QRenderJava3D -  La entidad ya existe " + entidad.getNombre() + " en el indice " + escenaJava3D.indexOfChild(tmp));
                            } else {
                                try {
                                    escenaJava3D.setCapability(Group.ALLOW_CHILDREN_EXTEND);
                                    escenaJava3D.setCapability(Group.ALLOW_CHILDREN_READ);
                                    escenaJava3D.setCapability(Group.ALLOW_CHILDREN_WRITE);
                                    raiz.addChild(tmp);
                                    agregados.put(entidad.getRutaPadres(), lista);
                                    QLogger.info("QRenderJava3D - Se agrego la entidad " + entidad.getNombre());
                                } catch (Exception e) {
                                    QLogger.info("QRenderJava3D - Error al agregar la entidad " + entidad.getNombre());
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            QLogger.info("QRenderJava3D - La entidad es nula " + entidad.getNombre());
                            QLogger.info("Tiene componentes ? " + entidad.getComponentes().size());
                        }
                    }
                }
            } catch (Exception e) {
                QLogger.info("QRenderJava3D - Error al agregar la entidad " + entidad.getNombre());
                e.printStackTrace();
            }
//            //hijos
//            if (objeto.getHijos() != null) {
//                for (QEntidad hijo : objeto.getHijos()) {
//                    agregarObjeto(hijo, raiz);
//                }
//            }
//            //hijos
//            if (objeto.getHijos() != null) {
//                for (QEntidad hijo : objeto.getHijos()) {
//                    agregarObjeto(hijo, raiz);
//                }
//            }
        }
    }

    private void crearEscena() {
        try {
            raiz = new OrderedGroup();
            if (escenaJava3D == null) {
                escenaJava3D = new BranchGroup();
                escenaJava3D.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
                escenaJava3D.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
                escenaJava3D.setCapability(Group.ALLOW_CHILDREN_EXTEND);
                escenaJava3D.setCapability(Group.ALLOW_CHILDREN_READ);
                escenaJava3D.setCapability(Group.ALLOW_CHILDREN_WRITE);

                escenaJava3D.addChild(raiz);

                BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);

//                //crea la neblina
//                if (super.escena.neblina != null && super.escena.neblina.isActive()) {
//                    LinearFog fog = new LinearFog();
//                    fog.setColor(QVectMathUtil.convertirColor3f(super.escena.neblina.getColour()));
//                    fog.setFrontDistance(super.escena.neblina.getDensity());
//                    fog.setBackDistance(super.escena.neblina.getDensity() + 20);
//                    fog.setCapability(Fog.ALLOW_COLOR_WRITE);
//                    fog.setCapability(LinearFog.ALLOW_DISTANCE_WRITE);
//                    fog.setInfluencingBounds(bounds);
//                    raiz.addChild(fog);
//                }
                //agrego luz ambiente
                // luces
//direccional
//                Color3f light1Color = new Color3f(1f, 1f, 1f);
//                Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
//                DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
//                light1.setInfluencingBounds(bounds);
//                raiz.addChild(light1);
//ambiente
//                 ambientColor = new Color3f(1f, 1f, 1f);
                ambientColor = new Color3f(escena.getColorAmbiente().r, escena.getColorAmbiente().g, escena.getColorAmbiente().b);
//                 ambientColor = new Color3f(0.5f, 0.5f, 0.5f);
                AmbientLight ambientLightNode = new AmbientLight(ambientColor);
                ambientLightNode.setInfluencingBounds(bounds);
                raiz.addChild(ambientLightNode);
//carga un objeto OBJ                
                try {
                    ObjectFile f = new ObjectFile();
                    Scene s = f.load("/home/alberto/grive/desarrollo/NetBeansProjects/Engine3D/objetos/formato_obj/VEGETACION/EXTERIOR/alta_calidad/Tree/Tree.obj");
                    escenaJava3D.addChild(s.getSceneGroup());
                } catch (Exception e) {

                }
            }
//            escenaJava3D.removeAllChildren();
//        
//        escenaJava3D.addChild(dibujaFondo());
//        escenaJava3D.addChild(dibujaBola());

            for (QEntidad entidad : super.escena.getListaEntidades()) {
                agregarEntidad(entidad, raiz);
            }
//        
//            TransformGroup tg = new TransformGroup();
//            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//
//            MouseRotate myMouseRotate = new MouseRotate();
//            myMouseRotate.setTransformGroup(tg);
//            myMouseRotate.setSchedulingBounds(new BoundingSphere());
//            escena.addChild(myMouseRotate);
//            //zoom
//            MouseZoom myMouseZoom = new MouseZoom();
//            myMouseZoom.setTransformGroup(tg);
//            myMouseZoom.setSchedulingBounds(new BoundingSphere());
//            escena.addChild(myMouseZoom);
//            escena.compile();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Actualiza el estado del objeto y sus hijos
     *
     * @param objeto
     */
    private void actualizarObjeto(QEntidad entidad) {
        if (entidad == null) {
            return;
        }
        TransformGroup tmp;
        Transform3D transform = new Transform3D();
//        Node nodo = agregados.get(entidad.getRutaPadres());
        List<Node> nodos = agregados.get(entidad.getRutaPadres());
        if (nodos != null && !nodos.isEmpty()) {
            for (Node nodo : nodos) {
                if (nodo != null) {
                    if (nodo instanceof TransformGroup) {
                        tmp = (TransformGroup) nodo;
                        tmp.getTransform(transform);
                        long t = System.currentTimeMillis();
                        transform.setScale(QVectMathUtil.convertirVector3d(entidad.getTransformacion().getEscala()));
                        QVector3 posicion = entidad.getMatrizTransformacion(t).toTranslationVector();
//                QVector3 posicion = entidad.transformacion.getTraslacion();
                        transform.setTranslation(QVectMathUtil.convertirVector3f(posicion));
//                QRotacion rot = new QRotacion();
//                rot.setCuaternion(entidad.getMatrizTransformacion(t).toRotationQuat());
//                rot.actualizarAngulos();
//                Transform3D t1 = new Transform3D();
//                Transform3D t2 = new Transform3D();
//                Transform3D t3 = new Transform3D();
//                t1.rotX(rot.getAngulos().getAnguloX());
//                t2.rotY(rot.getAngulos().getAnguloY());
//                t3.rotZ(rot.getAngulos().getAnguloZ());
//                t2.mul(t1);
//                t3.mul(t2);
//                transform.mul(t3);
                        QMatriz3 matRota = entidad.getMatrizTransformacion(t).toRotationQuat().toRotationMatrix();
                        transform.setRotation(QVectMathUtil.convertirMatriz3f(matRota));
                        tmp.setTransform(transform);
//                        QLogger.info("QRenderJava3D - Entidad " + entidad.nombre + " actualizada posicion");

                        //actualiza apariencia (textura) en caso que sea animada o modificada
                        //busco el componente de geometria
//                QGeometria geometria;
//                for (QComponente componente : entidad.componentes) {
//                    if (componente instanceof QGeometria) {
//                        try {
//                            geometria = (QGeometria) componente;
//                            if (geometria.faceList[0].material.getMapaDifusa() != null) {
//                                Appearance apariencia = ((Shape3D) ((TransformGroup) nodo).getChild(0)).getAppearance();
//                                BufferedImage bi = geometria.faceList[0].material.getMapaDifusa().getImagen();
//                                if (bi != null) {
//                                    TextureLoader loader = new TextureLoader(bi);
//                                    Texture texture = loader.getImagen();
//                                    //estos atricbutos permite que la textura sea iluminada, aun no se el porque.
//                                    TextureAttributes texAttr = new TextureAttributes();
////                                    texAttr.setTextureMode(TextureAttributes.MODULATE);
//                                    texAttr.setTextureMode(TextureAttributes.REPLACE);
////                                    texAttr.setTextureMode(TextureAttributes.COMBINE);
////                                    texAttr.setTextureMode(TextureAttributes.BLEND);
//
//                                    texture.setBoundaryModeS(Texture.WRAP);
//                                    texture.setBoundaryModeT(Texture.WRAP);
//                                    apariencia.setTextureAttributes(texAttr);
//                                    apariencia.setTexture(texture);
//
//                                    //transparencia la seteo nuevamente porq puede cambiar y es sobreescrita por la textura
//                                    TransparencyAttributes t_attr = new TransparencyAttributes();
//                                    t_attr.setTransparencyMode(TransparencyAttributes.BLENDED);
//                                    t_attr.setTransparency(1.0f - geometria.faceList[0].material.alpha);
//                                    apariencia.setTransparencyAttributes(t_attr);
//                                } else {
//                                    QLogger.info("EL BI ES NULO !! " + entidad.nombre);
//                                }
//                            }
//                        } catch (Exception e) {
//                            QLogger.info("error al intentar actualizar textura de " + entidad.nombre);
//                            e.printStackTrace();
//                        }
//                    }
//                }
                    } else {
                        System.out.println("QRenderJava3D - Se intenta modificar una entidad que no es de tipo TransformGroup " + entidad.getRutaPadres());
                    }
                } else {
                    System.out.println("no se encontro en los agregados a " + entidad.getRutaPadres());
                }
            }
        }

//        //hijos
//        if (entidad.getHijos() != null) {
//            for (QEntidad hijo : entidad.getHijos()) {
//                actualizarObjeto(hijo);
//            }
//        }
    }

//    @Override
    public void actualizarObjetos() {
        try {
            //actualizo la transformacion de los objetos
            for (QEntidad entidad : this.escena.getListaEntidades()) {
                actualizarObjeto(entidad);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void dibujarPixel(int x, int y) {
//    }
    private void modificadoresGeometria() {
        boolean modificado = false;
        QEmisorParticulas emisor;
        //se aplica las emisiones de particulas
        for (QEntidad entidad : super.escena.getListaEntidades()) {
            for (QComponente componente : entidad.getComponentes()) {
                //particulas
                if (componente instanceof QEmisorParticulas) {
                    emisor = (QEmisorParticulas) componente;
                    emisor.emitir(QTime.delta);
                    for (QParticula particula : emisor.getParticulasNuevas()) {
                        super.escena.agregarEntidad(particula.objeto);
                        modificado = true;
                    }
                }
            }

//                    for (QParticula particula : emisor.getParticulasEliminadas()) {
//                        escena.eliminarGeometria(particula.objeto);
//                        particula.objeto.renderizar=false;
//                        modificado = true;
//                    }
            //agrego las particulas nueva y elimino las viejas
        }
        if (modificado) {
//                    actualizarObjetosYLuces();
//                    updateVertexAndFaceSpace();
//                    updateView();
        }
    }

//    private void modificadoresTexturas() {
//        if (opciones.tipoVista == QOpcionesRenderer.VISTA_WIRE || !opciones.material) {
//            return;
//        }
//        boolean modificado = false;
//
//        for (QEntidad entidad : super.escena.getListaEntidades()) {
//            for (QComponente componente : entidad.getComponentes()) {
//                if (componente instanceof QProcesador) {
//                    if (renderReal) {
////                        ((QProcesador) componente).procesar(camara, super.escena);
//                        ((QProcesador) componente).procesar(this, super.escena);
//                    }
//                }
//            }
//        }
//        if (modificado) {
//
//        }
//    }
}
