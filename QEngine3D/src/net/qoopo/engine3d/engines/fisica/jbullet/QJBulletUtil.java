/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.fisica.jbullet;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.ConeShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.collision.shapes.CylinderShapeX;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.collision.shapes.TriangleShape;
import com.bulletphysics.dom.HeightfieldTerrainShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.vehicle.DefaultVehicleRaycaster;
import com.bulletphysics.dynamics.vehicle.RaycastVehicle;
import com.bulletphysics.dynamics.vehicle.VehicleRaycaster;
import com.bulletphysics.dynamics.vehicle.VehicleTuning;
import com.bulletphysics.dynamics.vehicle.WheelInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import java.nio.ByteBuffer;
import javax.vecmath.Vector3f;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.compuesta.QColisionCompuesta;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.compuesta.QColisionCompuestaHija;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaConvexa;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaIndexada;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionTerreno;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCaja;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCapsula;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCilindro;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCilindroX;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCono;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionEsfera;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionTriangulo;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QRueda;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QVehiculo;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.terreno.QTerreno;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QVectMathUtil;

/**
 * Utilidades para trabajar con JBullet
 *
 * @author alberto
 */
public class QJBulletUtil {

    public static Transform getTransformacion(QEntidad entidad) {
        Transform transInicial = new Transform();
        transInicial.setIdentity();
        transInicial.origin.set(QVectMathUtil.convertirVector3f(entidad.getTransformacion().getTraslacion()));
        transInicial.setRotation(QVectMathUtil.convertirQuat4f(entidad.getTransformacion().getRotacion().getCuaternion()));
        return transInicial;
    }

    /**
     * Crea un objeto rígido de jBullet
     *
     * @param rigido
     * @return
     */
    public static RigidBody crearRigido(QObjetoRigido rigido) {
        try {
            CollisionShape colShape = QJBulletUtil.getFormaColision(rigido.formaColision);
            if (colShape == null) {
                System.out.println("ERROR AL AGREGAR OBJETO RIGIDO, NO HAY FORMA DE COLISION " + rigido.entidad.getNombre());
                return null;
            }
//                formasColision.add(colShape);

            float mass = rigido.getMasa();

            // rigidbody is dynamic if and only if mass is non zero,
            // otherwise static
            boolean esDinamico = (mass != 0f) && rigido.tipoDinamico == QObjetoDinamico.DINAMICO;

            Vector3f localInertia = new Vector3f(0, 0, 0);
            if (esDinamico) {
                colShape.calculateLocalInertia(mass, localInertia);
            } else {
                mass = 0;// fuerzo la masa a 0
            }

            // using motionstate is recommended, it provides
            // interpolation capabilities, and only synchronizes
            // 'active' objects
            DefaultMotionState myMotionState = new DefaultMotionState(getTransformacion(rigido.entidad));

            RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);

            RigidBody body = new RigidBody(rbInfo);

            body.setFriction(rigido.getFriccion());
            body.setRestitution(rigido.getRestitucion());
            body.setDamping(rigido.getAmortiguacion_traslacion(), rigido.getAmortiguacion_rotacion());

            //agrego los fuerzas con las que llega el objeto
//                body.applyCentralForce(new Vector3f(rigido.fuerzaTotal.x, rigido.fuerzaTotal.y, rigido.fuerzaTotal.z));
//                body.applyCentralImpulse(new Vector3f(rigido.fuerzaTotal.x, rigido.fuerzaTotal.y, rigido.fuerzaTotal.z));
//                body.applyCentralForce(QVectMathUtil.convertirVector3f(rigido.fuerzaTotal));
            body.applyCentralImpulse(QVectMathUtil.convertirVector3f(rigido.fuerzaTotal));

            body.setUserPointer(rigido.entidad);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //------------------------------------------------------------------------------------------
    //---------------------- FORMAS DE COLISIÓN ------------------------------------------------
    //------------------------------------------------------------------------------------------
    /**
     * Construye una forma de colisión para JBullet
     *
     * @param formaColision
     * @return
     */
    public static CollisionShape getFormaColision(QFormaColision formaColision) {

//        com.bulletphysics.collision.shapes.BvhTriangleMeshShape
//HeightfieldTerrainShape shape;
//com.bulletphysics.collision.shapes.TriangleShape()
        CollisionShape colShape = null;
        if (formaColision instanceof QColisionEsfera) {
            QColisionEsfera contenedor = (QColisionEsfera) formaColision;
            colShape = new SphereShape(contenedor.getRadio());
        } else if (formaColision instanceof QColisionTriangulo) {
            QColisionTriangulo contenedor = (QColisionTriangulo) formaColision;
            colShape = new TriangleShape(QVectMathUtil.convertirVector3f(contenedor.getP1()), QVectMathUtil.convertirVector3f(contenedor.getP2()), QVectMathUtil.convertirVector3f(contenedor.getP3()));
        } else if (formaColision instanceof QColisionCaja) {
            QColisionCaja contenedor = (QColisionCaja) formaColision;
            colShape = new BoxShape(new Vector3f(contenedor.getAncho() / 2, contenedor.getAlto() / 2, contenedor.getLargo() / 2));
        } else if (formaColision instanceof QColisionCapsula) {
            QColisionCapsula contenedor = (QColisionCapsula) formaColision;
            colShape = new CapsuleShape(contenedor.getRadio(), contenedor.getAltura());
        } else if (formaColision instanceof QColisionCilindro) {
            QColisionCilindro contenedor = (QColisionCilindro) formaColision;
            colShape = new CylinderShape(new Vector3f(contenedor.getRadio(), contenedor.getAltura() / 2, contenedor.getRadio()));
        } else if (formaColision instanceof QColisionCilindroX) {
            QColisionCilindroX contenedor = (QColisionCilindroX) formaColision;
            colShape = new CylinderShapeX(new Vector3f(contenedor.getRadio(), contenedor.getAltura() / 2, contenedor.getRadio()));
        } else if (formaColision instanceof QColisionCono) {
            QColisionCono contenedor = (QColisionCono) formaColision;
            colShape = new ConeShape(contenedor.getRadio(), contenedor.getAltura());
        } else if (formaColision instanceof QColisionTerreno) {
            QColisionTerreno contenedor = (QColisionTerreno) formaColision;
            colShape = QJBulletUtil.getFormaColisionTerreno(contenedor.getTerreno());
        } else if (formaColision instanceof QColisionMallaConvexa) {
            QColisionMallaConvexa contenedor = (QColisionMallaConvexa) formaColision;
            colShape = QJBulletUtil.getFormaColisionMallaConvexa(contenedor.getMalla());
        } else if (formaColision instanceof QColisionMallaIndexada) {
            QColisionMallaIndexada contenedor = (QColisionMallaIndexada) formaColision;
            colShape = QJBulletUtil.getFormaColisionTriangleMesShape(contenedor.getMalla());
//            colShape = QJBulletUtil.getIndexedMesh(formaColision.getMalla());
        } else if (formaColision instanceof QColisionCompuesta) {
            QColisionCompuesta contenedor = (QColisionCompuesta) formaColision;

            colShape = QJBulletUtil.getFormaColisionCompuesta(contenedor);

        } else if (formaColision instanceof AABB) {
            float alto, ancho, largo;
            AABB aabb = (AABB) formaColision;
            ancho = Math.abs(aabb.aabMaximo.ubicacion.x - aabb.aabMinimo.ubicacion.x);
            alto = Math.abs(aabb.aabMaximo.ubicacion.y - aabb.aabMinimo.ubicacion.y);
            largo = Math.abs(aabb.aabMaximo.ubicacion.z - aabb.aabMinimo.ubicacion.z);
            colShape = new BoxShape(new Vector3f(ancho / 2, alto / 2, largo / 2));
        }

        //Escalo la forma de colisión de acuerdo a la escala de la entidad
        try {
            colShape.setLocalScaling(QVectMathUtil.convertirVector3f(formaColision.entidad.getTransformacion().getEscala()));
        } catch (Exception e) {
        }
        return colShape;
    }

    public static HeightfieldTerrainShape getFormaColisionTerreno(QTerreno terreno) {
        try {
            float[] data = new float[terreno.getPlanosAncho() * terreno.getPlanosLargo()];
            int c = 0;
            for (int x = 0; x < terreno.getPlanosAncho(); x++) {
                for (int z = 0; z < terreno.getPlanosAncho(); z++) {
                    data[c] = terreno.getAltura()[x][z];
                    c++;
                }
            }
            HeightfieldTerrainShape tt = new HeightfieldTerrainShape(terreno.getPlanosAncho(), terreno.getPlanosLargo(), data, 1.0f, terreno.getMinY(), terreno.getMaxY(), 1, false);
            return tt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConvexHullShape getFormaColisionMallaConvexa(QGeometria malla) {
        try {
            ObjectArrayList<Vector3f> lst = new ObjectArrayList(malla.listaVertices.length);
            for (QVertice vert : malla.listaVertices) {
                lst.add(new Vector3f(vert.ubicacion.x, vert.ubicacion.y, vert.ubicacion.z));
            }
            return new ConvexHullShape(lst);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BvhTriangleMeshShape getFormaColisionTriangleMesShape(QGeometria malla) {
        try {
            boolean useQuantizedAabbCompression = true;
            IndexedMesh tmp = getIndexedMesh(malla);
            TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(
                    tmp.numTriangles,
                    tmp.triangleIndexBase,
                    tmp.triangleIndexStride,
                    tmp.numVertices, tmp.vertexBase,
                    tmp.vertexStride);
            BvhTriangleMeshShape salida = new BvhTriangleMeshShape(indexVertexArrays, useQuantizedAabbCompression);
            return salida;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * Malla compleja
     *
     * @param mesh
     * @return
     */
    public static synchronized IndexedMesh getIndexedMesh(QGeometria mesh) {
        try {
            IndexedMesh jBulletIndexedMesh = new IndexedMesh();
            jBulletIndexedMesh.triangleIndexBase = ByteBuffer.allocate(mesh.listaPrimitivas.length * 3 * 4);//3 verts * 4 bytes per.
            jBulletIndexedMesh.vertexBase = ByteBuffer.allocate(mesh.listaVertices.length * 3 * 4);//3 coords * 4 bytes per.

//        IndexBuffer indices = mesh.getIndicesAsList();
//        FloatBuffer vertices = mesh.getFloatBuffer(Type.Position);
//        vertices.rewind();
            int verticesLength = mesh.listaVertices.length;
            jBulletIndexedMesh.numVertices = mesh.listaVertices.length;
            jBulletIndexedMesh.vertexStride = 12; //3 verts * 4 bytes per.
            for (int i = 0; i < verticesLength; i++) {
                jBulletIndexedMesh.vertexBase.putFloat(mesh.listaVertices[i].ubicacion.x);
                jBulletIndexedMesh.vertexBase.putFloat(mesh.listaVertices[i].ubicacion.y);
                jBulletIndexedMesh.vertexBase.putFloat(mesh.listaVertices[i].ubicacion.z);
            }

            int indicesLength = mesh.listaPrimitivas.length;
            jBulletIndexedMesh.numTriangles = indicesLength;
            jBulletIndexedMesh.triangleIndexStride = 12; //3 index entries * 4 bytes each.
            for (int i = 0; i < indicesLength; i++) {
                jBulletIndexedMesh.triangleIndexBase.putInt(mesh.listaPrimitivas[i].listaVertices[0]);
                jBulletIndexedMesh.triangleIndexBase.putInt(mesh.listaPrimitivas[i].listaVertices[1]);
                jBulletIndexedMesh.triangleIndexBase.putInt(mesh.listaPrimitivas[i].listaVertices[2]);
            }

            return jBulletIndexedMesh;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CompoundShape getFormaColisionCompuesta(QColisionCompuesta compuesta) {
        try {
            CompoundShape salida = new CompoundShape();
            Transform t = new Transform();
            for (QColisionCompuestaHija hijo : compuesta.getHijos()) {
                t.setIdentity();
                t.origin.set(hijo.getUbicacion().x, hijo.getUbicacion().y, hijo.getUbicacion().z);
                salida.addChildShape(t, getFormaColision(hijo.getForma()));
            }
            return salida;

//            salida.
        } catch (Exception e) {

        }
        return null;
    }

    //------------------------------------------------------------------------------------------
    //---------------------- VEHÍCULO ------------------------------------------------
    //------------------------------------------------------------------------------------------
    // By default, Bullet Vehicle uses Y as up axis.
    // You can override the up axis, for example Z-axis up. Enable this define to see how to:
    // //#define FORCE_ZAXIS_UP 1
    //#ifdef FORCE_ZAXIS_UP
    //int rightIndex = 0;
    //int upIndex = 2;
    //int forwardIndex = 1;
    //btVector3 wheelDirectionCS0(0,0,-1);
    //btVector3 wheelAxleCS(1,0,0);
    //#else
    private static final int rightIndex = 0;
    private static final int upIndex = 1;
    private static final int forwardIndex = 2;
    private static final Vector3f wheelDirectionCS0 = new Vector3f(0, -1, 0);
    private static final Vector3f wheelAxleCS = new Vector3f(-1, 0, 0);
    //#endif

    public static RaycastVehicle crearVehiculo(QVehiculo qvehiculo, DynamicsWorld mundoDinamico) {
        RigidBody chasis = crearRigido(qvehiculo.getChasis());
        return crearVehiculo(qvehiculo, chasis, mundoDinamico);
    }

    public static RaycastVehicle crearVehiculo(QVehiculo qvehiculo, RigidBody chasis, DynamicsWorld mundoDinamico) {
        RaycastVehicle vehiculo = null;
        VehicleTuning tuning = new VehicleTuning();
        VehicleRaycaster vehicleRayCaster = new DefaultVehicleRaycaster(mundoDinamico);

        vehiculo = new RaycastVehicle(tuning, chasis, vehicleRayCaster);

        // never deactivate the vehiculo
        chasis.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        float connectionHeight = 1.2f;

        boolean isFrontWheel = true;

        // choose coordinate system
        vehiculo.setCoordinateSystem(rightIndex, upIndex, forwardIndex);

        Vector3f connectionPointCS0 = new Vector3f();
        int CUBE_HALF_EXTENTS = 1;
        for (QRueda rueda : qvehiculo.getRuedas()) {
//            connectionPointCS0.set(CUBE_HALF_EXTENTS - (0.3f * rueda.getAncho()), connectionHeight, 2f * CUBE_HALF_EXTENTS - rueda.getRadio());
            connectionPointCS0.set(QVectMathUtil.convertirVector3f(rueda.getEntidadRueda().getMatrizTransformacion(QGlobal.tiempo).toTranslationVector()));
            WheelInfo info = vehiculo.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, rueda.getSuspensionRestLength(), rueda.getRadio(), tuning, isFrontWheel);
            info.suspensionStiffness = rueda.getSuspensionStiffness();
            info.wheelsDampingRelaxation = rueda.getDampingRelaxation();
            info.wheelsDampingCompression = rueda.getDampingCompression();
            info.frictionSlip = rueda.getFriccion();
            info.rollInfluence = rueda.getInfluenciaRodamiento();
        }

//        connectionPointCS0.set(CUBE_HALF_EXTENTS - (0.3f * wheelWidth), connectionHeight, 2f * CUBE_HALF_EXTENTS - wheelRadius);
//        vehiculo.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);
//        connectionPointCS0.set(-CUBE_HALF_EXTENTS + (0.3f * wheelWidth), connectionHeight, 2f * CUBE_HALF_EXTENTS - wheelRadius);
//        vehiculo.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);
//        
//        isFrontWheel = false;
//        connectionPointCS0.set(-CUBE_HALF_EXTENTS + (0.3f * wheelWidth), connectionHeight, -2f * CUBE_HALF_EXTENTS + wheelRadius);
//        vehiculo.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);
//        connectionPointCS0.set(CUBE_HALF_EXTENTS - (0.3f * wheelWidth), connectionHeight, -2f * CUBE_HALF_EXTENTS + wheelRadius);
//        vehiculo.addWheel(connectionPointCS0, wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);
//        for (int i = 0; i < vehiculo.getNumWheels(); i++) {
//            WheelInfo wheel = vehiculo.getWheelInfo(i);
//            wheel.suspensionStiffness = suspensionStiffness;
//            wheel.wheelsDampingRelaxation = suspensionDamping;
//            wheel.wheelsDampingCompression = suspensionCompression;
//            wheel.frictionSlip = wheelFriction;
//            wheel.rollInfluence = rollInfluence;
//        }
        return vehiculo;
    }
}
