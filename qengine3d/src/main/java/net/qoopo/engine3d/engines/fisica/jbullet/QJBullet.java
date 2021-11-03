/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.fisica.jbullet;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.AxisSweep3_32;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.SimpleBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.vehicle.RaycastVehicle;
import com.bulletphysics.linearmath.Transform;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.fisica.colisiones.listeners.QListenerColision;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.fisica.restricciones.QRestriccion;
import net.qoopo.engine3d.componentes.fisica.restricciones.QRestriccionAmortiguador;
import net.qoopo.engine3d.componentes.fisica.restricciones.QRestriccionBisagra;
import net.qoopo.engine3d.componentes.fisica.restricciones.QRestriccionFija;
import net.qoopo.engine3d.componentes.fisica.restricciones.QRestriccionGenerica6DOF;
import net.qoopo.engine3d.componentes.fisica.restricciones.QRestriccionPunto2Punto;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QRueda;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QVehiculo;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QLogger;
import net.qoopo.engine3d.core.util.QVectMathUtil;
import net.qoopo.engine3d.engines.fisica.QMotorFisica;

/**
 * Implementacion de JButllet para QEngine
 *
 * @author alberto
 */
public class QJBullet extends QMotorFisica {

    /**
     * interface with Broadphase types
     */
    public enum BroadphaseType {

        /**
         * basic Broadphase
         */
        SIMPLE,
        /**
         * better Broadphase, needs worldBounds , max Object number = 16384
         */
        AXIS_SWEEP_3,
        /**
         * better Broadphase, needs worldBounds , max Object number = 65536
         */
        AXIS_SWEEP_3_32,
        /**
         * Broadphase allowing quicker adding/removing of physics objects
         */
        DBVT;
    }

    protected DecimalFormat df = new DecimalFormat("0.00");

    // collision configuration contains default setup for memory, collision setup. Advanced users can create their own configuration.
    private CollisionConfiguration collisionConfiguration;
    // use the default collision dispatcher. For parallel processing you can use a diffent dispatcher (see Extras/BulletMultiThreaded)
    private CollisionDispatcher dispatcher;

    // the maximum size of the collision world. Make sure objects stay
    // within these boundaries
    // Don't make the world AABB size too large, it will harm simulation
    // quality and performance
    private Vector3f worldAabbMin = new Vector3f(-1000, -1000, -1000);
    private Vector3f worldAabbMax = new Vector3f(1000, 1000, 1000);
//    private final int maxProxies = 1024;
//    private AxisSweep3 overlappingPairCache;
    private BroadphaseInterface broadphase;
    // the default constraint solver. For parallel processing you can use a different solver (see Extras/BulletMultiThreaded)
    private SequentialImpulseConstraintSolver solver;
    private DiscreteDynamicsWorld mundoDinamicoDiscreto;

    // keep track of the shapes, we release memory at exit. make sure to re-use collision shapes among rigid bodies whenever possible!
//    private ObjectArrayList<CollisionShape> formasColision = new ObjectArrayList<>();
    private final Map<String, QEntidad> mapaAgregados = new HashMap<>();
    private final Map<String, RigidBody> mapaRigidos = new HashMap<>();
    private final Map<String, RaycastVehicle> mapaRayCastVehiculos = new HashMap<>();
    private final Map<String, Integer> mapaRestricciones = new HashMap<>();// mapa para no volver a agregar las restricciones

    //-------------------------------
//    private BroadphaseType broadphaseType = BroadphaseType.DBVT;
    private BroadphaseType broadphaseType = BroadphaseType.AXIS_SWEEP_3;

    public QJBullet(QEscena universo) {
        super(universo);
        tiempoPrevio = System.currentTimeMillis();
        iniciarMundoFisico();
    }

    public QJBullet(QEscena universo, Vector3f worldAabbMin, Vector3f worldAabbMax) {
        super(universo);
        tiempoPrevio = System.currentTimeMillis();
        this.worldAabbMax = worldAabbMax;
        this.worldAabbMin = worldAabbMin;
        iniciarMundoFisico();
    }

    private void iniciarMundoFisico() {
        collisionConfiguration = new DefaultCollisionConfiguration();
        dispatcher = new CollisionDispatcher(collisionConfiguration);
        //BroadphaseInterface overlappingPairCache = new SimpleBroadphase(maxProxies);
        // the default constraint solver. For parallel processing you can use a different solver (see Extras/BulletMultiThreaded)
        solver = new SequentialImpulseConstraintSolver();
//        overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
        switch (broadphaseType) {
            case SIMPLE:
                broadphase = new SimpleBroadphase();
                break;
            case AXIS_SWEEP_3:
                broadphase = new AxisSweep3(worldAabbMin, worldAabbMax);
                break;
            case AXIS_SWEEP_3_32:
                broadphase = new AxisSweep3_32(worldAabbMin, worldAabbMax);
                break;
            case DBVT:
                broadphase = new DbvtBroadphase();
                break;
        }
        mundoDinamicoDiscreto = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        mundoDinamicoDiscreto.setGravity(QVectMathUtil.convertirVector3f(universo.gravedad));
//        broadphase.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
    }

    @Override
    public void iniciar() {
        ejecutando = true;
    }

    @Override
    public long update() {
        actualizarMundoDinamico();
        try {
            mundoDinamicoDiscreto.stepSimulation(getDelta() / 1000.0f, 10);
        } catch (Exception e) {
            System.out.println("MF. Error=" + e.getMessage());
            e.printStackTrace();
        }
        actualizarUniversoQEngine();
        limpiarFuerzas();
        detectarColisiones();
        QLogger.info("MF-->" + df.format(getFPS()) + " FPS");
        tiempoPrevio = System.currentTimeMillis();
        return tiempoPrevio;
    }

    /**
     * Actualizo la lista de los objetos dinamicos para que corresponda al
     * universo del engine
     */
    private void actualizarMundoDinamico() {
        try {
            actualizarABBS();
            QObjetoRigido actual;
            //primero se agregan objetos rigidos
            for (QEntidad objeto : universo.getListaEntidades()) {
                if (objeto.isRenderizar()) {
                    for (QComponente componente : objeto.getComponentes()) {
                        //objeto rígido
                        if (componente instanceof QObjetoRigido && !((QObjetoRigido) componente).isUsado()) {
                            actual = (QObjetoRigido) componente;
                            if (!mapaAgregados.containsKey(objeto.getNombre())) {
                                agregarRigido(actual);
                                mapaAgregados.put(objeto.getNombre(), objeto);
                            } else {
                                actualizarRigido(actual);
                            }
                        } else if (componente instanceof QVehiculo) {
                            if (!mapaRayCastVehiculos.containsKey(objeto.getNombre())) {
                                RigidBody chasis = agregarRigido(((QVehiculo) componente).getChasis());
                                RaycastVehicle vehiculo = QJBulletUtil.crearVehiculo((QVehiculo) componente, chasis, mundoDinamicoDiscreto);
                                mapaRayCastVehiculos.put(objeto.getNombre(), vehiculo);
                                mundoDinamicoDiscreto.addVehicle(vehiculo);
                            } else {
                                // Actualiza la aceleración y frenos del vehículo (actualmente ase asume 4 ruedas
                                RaycastVehicle vehiculo = mapaRayCastVehiculos.get(objeto.getNombre());
                                QVehiculo comp = (QVehiculo) componente;
                                int c = 0;
                                for (QRueda rueda : comp.getRuedas()) {
                                    if (rueda.isFrontal()) {
                                        //detalenteras
                                        vehiculo.setSteeringValue(comp.getgVehicleSteering(), c);
                                    }
                                    vehiculo.applyEngineForce(comp.getgEngineForce(), c);//aplica aceleracion
                                    vehiculo.setBrake(comp.getgBreakingForce(), c); // aplica freno
                                    c++;
                                }
                            }
                        }
                    }
                }
            }

            //luego se agregan restricciones, pues es necesario que existan los objetos rigidos previamente
            String nombreA;
            String nombreB;
            String idRes;
            //com.bulletphysics.dynamics.constraintsolver.
            for (QEntidad objeto : universo.getListaEntidades()) {
                if (objeto.isRenderizar()) {
                    for (QComponente componente : objeto.getComponentes()) {
                        if (componente instanceof QRestriccion) {
                            nombreA = ((QRestriccion) componente).getA().entidad.getNombre();
                            nombreB = ((QRestriccion) componente).getB().entidad.getNombre();
                            idRes = nombreA + "x" + nombreB;
                            if (componente instanceof QRestriccionFija) {

                            } else if (componente instanceof QRestriccionPunto2Punto) {
                                if (!mapaRestricciones.containsKey(idRes)) {
                                    Point2PointConstraint joint = new Point2PointConstraint(
                                            mapaRigidos.get(nombreA),
                                            mapaRigidos.get(nombreB),
                                            QVectMathUtil.convertirVector3f(((QRestriccionPunto2Punto) componente).getA().entidad.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector()),
                                            QVectMathUtil.convertirVector3f(((QRestriccionPunto2Punto) componente).getB().entidad.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector())
                                    );
                                    mundoDinamicoDiscreto.addConstraint(joint);
                                    mapaRestricciones.put(idRes, 1);
                                }
                            } else if (componente instanceof QRestriccionBisagra) {
                                if (!mapaRestricciones.containsKey(idRes)) {
                                    HingeConstraint joint = new HingeConstraint(
                                            mapaRigidos.get(nombreA),
                                            mapaRigidos.get(nombreB),
                                            QJBulletUtil.getTransformacion(((QRestriccionBisagra) componente).getA().entidad),
                                            QJBulletUtil.getTransformacion(((QRestriccionBisagra) componente).getB().entidad)
                                    );
                                    mundoDinamicoDiscreto.addConstraint(joint);
                                    mapaRestricciones.put(idRes, 1);
                                }
                            } else if (componente instanceof QRestriccionAmortiguador) {
                            } else if (componente instanceof QRestriccionGenerica6DOF) {

                                if (!mapaRestricciones.containsKey(idRes)) {
                                    Generic6DofConstraint joint = new Generic6DofConstraint(
                                            mapaRigidos.get(nombreA),
                                            mapaRigidos.get(nombreB),
                                            QJBulletUtil.getTransformacion(((QRestriccionGenerica6DOF) componente).getA().entidad),
                                            QJBulletUtil.getTransformacion(((QRestriccionGenerica6DOF) componente).getB().entidad),
                                            ((QRestriccionGenerica6DOF) componente).isUsarReferenciaLinearA()
                                    );
                                    //tomado del ejemplo de ragDoll
                                    Vector3f tmp = new Vector3f();
                                    tmp.set(-BulletGlobals.SIMD_PI * 0.8f, -BulletGlobals.SIMD_EPSILON, -BulletGlobals.SIMD_PI * 0.5f);
                                    joint.setAngularLowerLimit(tmp);
                                    tmp.set(BulletGlobals.SIMD_PI * 0.8f, BulletGlobals.SIMD_EPSILON, BulletGlobals.SIMD_PI * 0.5f);
                                    joint.setAngularUpperLimit(tmp);
                                    mundoDinamicoDiscreto.addConstraint(joint);
                                    mapaRestricciones.put(idRes, 1);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Crea los contenedores abbs para los que no se definio uno
    private void actualizarABBS() {
        try {
            for (QEntidad objeto : universo.getListaEntidades()) {
                if (objeto.isRenderizar()) {
                    QObjetoRigido componenteFisico = QUtilComponentes.getFisicoRigido(objeto);
//                           componenteFisico.tieneColision = false;
                    if (componenteFisico != null && componenteFisico.formaColision == null) {
                        QGeometria geometria = QUtilComponentes.getGeometria(objeto);
                        if (geometria != null) {
                            System.out.println(" Se va a crear objeto de colision para " + objeto.getNombre());
                            componenteFisico.crearContenedorColision(geometria, objeto.getTransformacion());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Agrega un nuevo rígido al mundo dinamico
     *
     * @param rigido
     * @return
     */
    public RigidBody agregarRigido(QObjetoRigido rigido) {
        switch (rigido.tipoDinamico) {
            case QObjetoDinamico.DINAMICO:
            case QObjetoDinamico.ESTATICO:

                RigidBody body = QJBulletUtil.crearRigido(rigido);
                mapaRigidos.put(rigido.entidad.getNombre(), body);
                mundoDinamicoDiscreto.addRigidBody(body);
                return body;

            case QObjetoDinamico.CINEMATICO:
                break;

            default:
                break;
        }
        return null;
    }

    /**
     * Actualiza las fuerzas que se hayan agregado al rigido mediante
     * programación y elimina del mundo a las entidades que se eliminaron del
     * universo del engine
     *
     * @param rigido
     */
    private void actualizarRigido(QObjetoRigido rigido) {
        try {
            RigidBody body = mapaRigidos.get(rigido.entidad.getNombre());
            if (body != null) {
                //si es de eliminar, lo eliminamos y saltamos actualizaciones
                if (rigido.entidad.isEliminar()) {
                    QLogger.info("QJBullet - Eliminando objeto rigido " + rigido.entidad.getNombre());
                    mundoDinamicoDiscreto.removeRigidBody(body);
                    mapaRigidos.remove(rigido.entidad.getNombre());
                } else {
                    body.applyCentralImpulse(QVectMathUtil.convertirVector3f(rigido.fuerzaTotal));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza las información de las entidades desde el mundo dinámico al
     * universo de QEngine
     */
    private void actualizarUniversoQEngine() {
        try {
            //----------------- ACTUALIZACIÓN DE RÍGIDOS
            for (int j = mundoDinamicoDiscreto.getNumCollisionObjects() - 1; j >= 0; j--) {
                CollisionObject obj = mundoDinamicoDiscreto.getCollisionObjectArray().getQuick(j);
                RigidBody body = RigidBody.upcast(obj);
                actualizarEntidadQEngine(body);
            }
            //----------------- ACTUALIZACIÓN DE VEHÍCULOS
            QVehiculo veh;
            for (QEntidad objeto : universo.getListaEntidades()) {
                if (objeto.isRenderizar()) {
                    veh = QUtilComponentes.getVehiculo(objeto);
                    if (veh != null) {
                        RaycastVehicle vehiculo = mapaRayCastVehiculos.get(objeto.getNombre());
                        int c = 0;
                        for (QRueda rueda : veh.getRuedas()) {
                            vehiculo.updateWheelTransform(c, true);
                            Transform trans = vehiculo.getWheelInfo(c).worldTransform;
                            actualizaTransformacionEntidad(trans, rueda.getEntidadRueda());
                            c++;
                        }
                        //actualizo la transformación del rídigo del vehículo
//                            RigidBody body = vehiculo.getRigidBody();
//                            actualizarEntidadQEngine(body);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la entidad que contiene al componente rígido
     *
     * @param body
     */
    private void actualizarEntidadQEngine(RigidBody body) {
        if (body != null && body.getMotionState() != null) {
            Transform trans = new Transform();
            body.getMotionState().getWorldTransform(trans);
            Object objeto = body.getUserPointer();
            if (objeto != null) {
                actualizaTransformacionEntidad(trans, ((QEntidad) objeto));
            } else {
                System.out.println("NO TIENE ASOCIADO UN OBJETO DEL ENGINE NUESTRO !!!!");
            }
        }
    }

    /**
     * Actualiza la transformación desde el mundo dinámico al mundo del engine
     *
     * @param trans
     * @param entidad
     */
    private void actualizaTransformacionEntidad(Transform trans, QEntidad entidad) {
        Quat4f rotacion = new Quat4f();
        try {
            //actualizacion de la posicion
            entidad.mover(trans.origin.x, trans.origin.y, trans.origin.z);
            //actualizacion de la rotación
            trans.getRotation(rotacion);
            entidad.getTransformacion().getRotacion().getCuaternion().set(rotacion.x, rotacion.y, rotacion.z, rotacion.w);
            entidad.getTransformacion().getRotacion().actualizarAngulos();
            //aplico control de posicion propio de este engine
            entidad.comprobarMovimiento();
        } catch (Exception e) {
            System.out.println("error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Detecta las colisiones y lanza los disparadores
     */
    private void detectarColisiones() {
        try {
            int numManiFold = mundoDinamicoDiscreto.getDispatcher().getNumManifolds();
            for (int i = 0; i < numManiFold; i++) {
                PersistentManifold contacto = mundoDinamicoDiscreto.getDispatcher().getManifoldByIndexInternal(i);

                RigidBody body0 = RigidBody.upcast((CollisionObject) contacto.getBody0());
                RigidBody body1 = RigidBody.upcast((CollisionObject) contacto.getBody1());

                Object objeto0 = body0.getUserPointer();
                Object objeto1 = body1.getUserPointer();
                if (objeto0 != null) {
                    List<QListenerColision> lista = QUtilComponentes.getColisionListeners((QEntidad) objeto0);
                    for (QListenerColision list : lista) {
                        list.colision((QEntidad) objeto0, (QEntidad) objeto1);
                    }
                }
                if (objeto1 != null) {
                    List<QListenerColision> lista = QUtilComponentes.getColisionListeners((QEntidad) objeto1);
                    for (QListenerColision list : lista) {
                        list.colision((QEntidad) objeto1, (QEntidad) objeto0);
                    }
                }

//            //puntos de contacto
//            int numContacts = contacto.getNumContacts();
//            for (int j = 0; j < numContacts; j++) {
//                ManifoldPoint pt = contacto.getContactPoint(j);
//                if (pt.getDistance() < 0.f) {
//                    Vector3f ptA = new Vector3f();
//                    ptA = pt.getPositionWorldOnA(ptA);
//                    Vector3f ptB = new Vector3f();
//                    ptB = pt.getPositionWorldOnB(ptB);
//
//                    Vector3f normalOnB = pt.normalWorldOnB;
//                }
//            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Limpia las fuerzas que se agregaron mediante código
     */
    private void limpiarFuerzas() {
        try {
            QObjetoRigido actual;
            for (QEntidad objeto : universo.getListaEntidades()) {
                if (objeto.isRenderizar()) {
                    QObjetoRigido rigido = QUtilComponentes.getFisicoRigido(objeto);
                    if (rigido != null) {
                        rigido.limpiarFuezas();
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
